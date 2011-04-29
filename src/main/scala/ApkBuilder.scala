import sbt._
import java.io.{File, PrintStream}

class ApkBuilder(project: AndroidProject, debug: Boolean) {
  
  val classLoader = ClasspathUtilities.toLoader(project.androidToolsPath / "lib" / "sdklib.jar")
  val klass = classLoader.loadClass("com.android.sdklib.build.ApkBuilder")
  val constructor = klass.getConstructor(
    classOf[File], classOf[File], classOf[File], classOf[String], classOf[PrintStream])
  val keyStore = if (debug) getDebugKeystore else null
  val builder = constructor.newInstance(
    project.packageApkPath.asFile, project.resourcesApkPath.asFile, project.classesDexPath.asFile, keyStore, null)
  setDebugMode(debug)

  def build() = {
    sealApk
    None
  }
  
  def getDebugKeystore = {
    val method = klass.getMethod("getDebugKeystore")
    method.invoke(null).asInstanceOf[String]
  }
  
  def setDebugMode(debug: Boolean) {
    val method = klass.getMethod("setDebugMode", classOf[Boolean])
    method.invoke(builder, debug.asInstanceOf[Object])
  }
  
  def sealApk() {
    val method = klass.getMethod("sealApk")
    method.invoke(builder)
  }
}
