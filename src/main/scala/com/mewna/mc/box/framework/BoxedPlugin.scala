package com.mewna.mc.box.framework

import com.mewna.mc.box.framework.command.annotation.Command
import com.mewna.mc.box.framework.command.{BoxedCommand, BukkitCommandInjector}
import com.mewna.mc.box.framework.component.{BoxedComponent, Component, Single}
import com.mewna.mc.box.framework.config.Config
import com.mewna.mc.box.framework.di.Auto
import com.mewna.mc.box.{BoxPlugin, Boxed}
import io.github.classgraph.{ClassGraph, ScanResult}
import org.bukkit.Bukkit
import org.bukkit.event.Listener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.reflect.runtime.{universe => ru}


/**
 * @author amy
 * @since 7/10/19
 */
object BoxedPlugin {
  private val instance: BoxedPlugin = new BoxedPlugin()
  
  def initialize[T <: BoxPlugin](plugin: T): Unit = {
    instance.scanAndInitFromPlugin(plugin)
  }
  
  def locateComponent[T](cls: Class[T], ctx: Map[Class[_ <: Any], Any] = Map()): Option[T] = instance.locateComponent(cls, ctx)
  
  def locateCommandComponent[T](cls: Class[T], ctx: Map[Class[_ <: Any], Any] = Map()): Option[T] = instance.locateCommandComponent(cls, ctx)
}

/**
 * State container
 *
 * @author amy
 * @since 7/10/19.
 */
private class BoxedPlugin {
  private var plugin: Option[_ <: BoxPlugin] = Option.empty
  private var graph: ScanResult = _
  private val components = mutable.HashSet[Class[_]]()
  private val commands = mutable.HashSet[Class[_]]()
  private val singletons = mutable.Map[Class[_], Any]()
  
  def scanAndInitFromPlugin[T <: BoxPlugin](p: T): Unit = {
    plugin = Option(p)
    val cls = p.getClass
    val boxed = cls.getDeclaredAnnotation(classOf[Boxed])
    val logger = p.getLogger
    if(boxed != null) {
      // Register the plugin itself as a component before anything else. This
      // ensures that it'll be available for the incoming components to have it
      // injected.
      singletons.put(p.getClass, p)
      // Scan the classpath for components, starting at the package that was
      // configured on our base plugin
      val pkg = boxed.value()
      graph = new ClassGraph().enableAllInfo().whitelistPackages(pkg).scan()
      loadComponents()
      // Once we've loaded the components, we can inject memes into them
      injectSingletonData()
      // Autoregister and inject all Bukkit listeners
      registerListeners()
      // Autoregister commands in a way that allows for instancing them
      registerCommands()
      logger.info(f"Finished loading @Boxed plugin ${p.getName}!")
    } else {
      throw new IllegalArgumentException(f"Passed plugin of type ${cls.getName}, but it's not @Boxed!")
    }
  }
  
  private def loadComponents(): Unit = {
    val logger = plugin.get.getLogger
    // Since we're doing it inside of Bukkit, all of these are
    // ~already loaded~ and so we can just set 'em up directly
    val componentClasses = graph.getClassesWithAnnotation(classOf[Component].getName)
    componentClasses.getNames.asScala.map(n => Class.forName(n)).foreach(c => {
      if(c.isAnnotationPresent(classOf[Single])) {
        // If it's explicitly marked as a singleton, just create an instance
        // directly and hold on to it for later
        singletons.put(c, c.getConstructor().newInstance())
        logger.info(f"Loaded new singleton component ${c.getName}")
      } else {
        // Otherwise, just store it and create new ones as needed.
        components.add(c)
        logger.info(f"Loaded new instanced component ${c.getName}")
      }
    })
  }
  
  private def injectSingletonData(): Unit = {
    val logger = plugin.get.getLogger
    singletons.values.foreach(v => {
      // Inject config
      injectConfig(plugin.get, v)
      // Inject components
      injectComponents(v)
      // Init the singleton boxed components as needed
      // We do this here rather than at load-time so that we can ensure that
      // all other components are actually present, ex. a sign shop relying
      // on the presence of a custom sign registry.
      v match {
        case e: BoxedComponent =>
          try {
            if(e.doInit(plugin.get)) {
              logger.info(f"Loaded component: ${e.getName}: ${e.getDesc}")
            } else {
              logger.info(f"Couldn't init component: ${e.getName}")
            }
          } catch {
            case ex: Exception =>
              logger.info(f"Exception during component init for ${e.getName}:")
              ex.printStackTrace()
          }
        case _ =>
      }
    })
  }
  
  private def registerListeners(): Unit = {
    val logger = plugin.get.getLogger
    val listenerClasses = graph.getClassesImplementing(classOf[Listener].getName)
    listenerClasses.getNames.asScala.map(n => Class.forName(n)).foreach(c => {
      val instance = c.getConstructor().newInstance().asInstanceOf[Listener]
      injectComponents(instance)
      injectConfig(plugin.get, instance)
      Bukkit.getPluginManager.registerEvents(instance, plugin.get)
      logger.info(f"Loaded new instanced Bukkit listener ${c.getName}")
    })
  }
  
  private def registerCommands(): Unit = {
    val logger = plugin.get.getLogger
    val commandClasses = graph.getClassesWithAnnotation(classOf[Command].getName)
    commandClasses.getNames.asScala.map(n => Class.forName(n)).foreach(c => {
      commands.add(c)
      val annotation = c.getDeclaredAnnotation(classOf[Command])
      val wrapper = new BoxedCommand(c, annotation.name(), annotation.desc(), annotation.usage(),
        annotation.aliases(), annotation.permissionNode(), annotation.permissionMessage())
      wrapper.setLabel(annotation.label())
      wrapper.loadSubcommands()
      BukkitCommandInjector.registerCommand(plugin.get, wrapper)
      logger.info(f"Injected new command ${annotation.name()}")
    })
  }
  
  def injectComponents[T](obj: T, ctx: Map[Class[_ <: Any], Any] = Map()): Unit = {
    obj.getClass.getDeclaredFields.filter(f => f.isAnnotationPresent(classOf[Auto])).foreach(f => {
      f.setAccessible(true)
      val t = f.getType
      // Try to grab a component from the context first, if applicable
      val ctxMatch = ctx.keySet.find(c => t.isAssignableFrom(c))
      if(ctxMatch.isDefined) {
        // ctx overrides known components in all cases
        f.set(obj, ctx(ctxMatch.get))
      } else {
        // If we have nothing in the ctx, then we grab a component the lazy way
        val component = locateNormalComponent(t, ctx)
        if(component.isDefined) {
          f.set(obj, component.get)
        } else {
          // TODO: How to log that we couldn't find a matching component?
        }
      }
    })
  }
  
  def injectConfig[P <: BoxPlugin, T](plugin: P, obj: T): Unit = {
    val config = plugin.getConfig
    val mirror = ru.runtimeMirror(obj.getClass.getClassLoader)
    
    obj.getClass.getDeclaredFields.filter(f => f.isAnnotationPresent(classOf[Config])).foreach(f => {
      f.setAccessible(true)
      val path = f.getDeclaredAnnotation(classOf[Config]).value()
      val cls = f.getType
      val t = mirror.classSymbol(cls).toType
      
      val value =
        if(t == ru.typeOf[Boolean]) {
          config.getBoolean(path)
        } else if(t == ru.typeOf[Double]) {
          config.getDouble(path)
        } else if(t == ru.typeOf[Int]) {
          config.getInt(path)
        } else if(t == ru.typeOf[Long]) {
          config.getLong(path)
        } else if(t == ru.typeOf[String]) {
          config.getString(path)
        } else {
          config.get(path)
        }
      f.set(obj, value)
    })
  }
  
  def locateComponent[T](cls: Class[T], ctx: Map[Class[_ <: Any], Any] = Map()): Option[T] = {
    val normal = locateNormalComponent(cls, ctx)
    if(normal.isDefined) {
      normal
    } else {
      locateCommandComponent(cls, ctx)
    }
  }
  
  private def locateNormalComponent[T](cls: Class[T], ctx: Map[Class[_ <: Any], Any] = Map()): Option[T] = {
    // Scan singletons
    val singleGet = singletons.get(cls)
    if(singleGet.isDefined) {
      singleGet.asInstanceOf[Option[T]]
    } else {
      // Scan singletons in a more thorough manner
      val singleClassMatch = singletons.keySet.find(c => cls.isAssignableFrom(c))
      if(singleClassMatch.isDefined) {
        // Fetch the singleton and return it
        singletons.get(singleClassMatch.get).asInstanceOf[Option[T]]
      } else {
        // Scan the instanced components for a match
        val option = components.find(c => c.isAssignableFrom(cls))
        if(option.isDefined) {
          val instance = option.get.getConstructor().newInstance().asInstanceOf[T]
          injectComponents(instance, ctx)
          injectConfig(plugin.get, instance)
          Option(instance)
        } else {
          Option.empty
        }
      }
    }
  }
  
  private def locateCommandComponent[T](cls: Class[T], ctx: Map[Class[_ <: Any], Any] = Map()): Option[T] = {
    // Commands can NEVER be singletons, so we just only scan the command classes
    val option = commands.find(c => cls.isAssignableFrom(c))
    if(option.isDefined) {
      val instance = option.get.getConstructor().newInstance().asInstanceOf[T]
      injectComponents(instance, ctx)
      injectConfig(plugin.get, instance)
      Option(instance)
    } else {
      Option.empty
    }
  }
}
