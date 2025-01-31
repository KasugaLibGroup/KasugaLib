package kasuga.builder


import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.bundling.Jar

import javax.inject.Inject
import org.gradle.api.Action

class PackerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.tasks.register('multiPlatformBinaryPack', MultiPlatformBinaryPack)
    }

    static class MultiPlatformBinaryPack extends Jar {
        public final HashSet<Platform> platforms = new HashSet<>()

        protected final HashMap<String, File> outputFiles = new HashMap<>()

        void platform(Action<? super Platform> action) {
            Platform platform = new Platform()
            action.execute(platform)
            platforms.add(platform)
            outputFiles.put(platform.name, new File("${project.buildDir}/libs/${project.archivesBaseName}-${project.version}-${platform.name}.jar"))
        }

        MultiPlatformBinaryPack() {
            // outputFiles.put("unified", new File("${project.buildDir}/libs/${project.archivesBaseName}-${project.version}.jar"))
        }

        // @TaskAction
        void listContents() {
            platforms.each { platform ->
                println "Platform: ${platform.name}"
                platform.dependencies.each { dep ->
                    println " - Dependency: ${dep.name}"
                    dep.resolve().each {
                        println "   - ${it.toString()}"
                    }
                }
            }
        }

        @TaskAction
        void pack() {
            // 获取 shadowJar 任务的输出
            File shadowJarFile = project.tasks.getByName('shadowJar').archiveFile.get().asFile

            println "ShadowJar file: ${shadowJarFile.path}"

            File unifiedTempDir = new File("${project.buildDir}/multiPlatformBinaryPack/unified")

            unifiedTempDir.delete()
            unifiedTempDir.mkdirs()

            project.copy {
                from project.zipTree(shadowJarFile)
                into unifiedTempDir
            }

            platforms.each { platform ->
                println "Packing for platform: ${platform.name}"


                // 创建一个临时目录用于解压和合并
                File tempDir = new File("${project.buildDir}/multiPlatformBinaryPack/${platform.name}")

                // 清理
                tempDir.delete()

                tempDir.mkdirs()

                // 解压 shadowJar 到临时目录
                project.copy {
                    from project.zipTree(shadowJarFile)
                    into tempDir
                }

                // 解压每个依赖的 Jar 文件到临时目录
                platform.dependencies.each { dep ->
                    dep.resolve().each { file ->
                        project.copy {
                            from project.zipTree(file)
                            into tempDir
                        }
                        project.copy {
                            from project.zipTree(file)
                            into unifiedTempDir
                        }
                    }
                }

                String version = project.version
                String baseName = project.archivesBaseName
                File outputJar = new File("${project.buildDir}/libs/${baseName}-${version}-${platform.name}.jar")
                project.ant.jar(destfile: outputJar) {
                    fileset(dir: tempDir)
                }

                println "Created combined Jar for platform: ${platform.name} at ${outputJar.path}"
            }

            String version = project.version
            String baseName = project.archivesBaseName
            File unifiedJar = new File("${project.buildDir}/libs/${baseName}-${version}.jar")
            project.ant.jar(destfile: unifiedJar) {
                fileset(dir: unifiedTempDir)
            }

            println "Created unified Jar at ${unifiedJar.path}"
        }

        @OutputFiles
        getOutputFiles() {
            return outputFiles
        }
    }

    static class Platform {
        String name
        
        @Classpath
        public ArrayList<Configuration> dependencies;

        void name(String name) {
            this.name = name
        }
    }
}