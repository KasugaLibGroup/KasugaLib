package kasuga.builder

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar

import org.gradle.api.Action

class PackerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.tasks.register('multiPlatformBinaryPack', MultiPlatformBinaryPack)
    }

    static class MultiPlatformBinaryPack extends DefaultTask {
        public final HashSet<Platform> platforms = new HashSet<>()
        protected final HashMap<String, File> outputFiles = new HashMap<>()

        void platform(Action<? super Platform> action) {
            Platform platform = new Platform()
            action.execute(platform)
            platforms.add(platform)
            
            // 为每个平台创建独立的 jar 任务
            String jarTaskName = "platformJar${platform.name.capitalize()}"
            String classifier = platform.getArchiveClassifier();

            File outputFile

            if(classifier == null || classifier.length() == 0){
                outputFile = new File("${project.buildDir}/libs/${project.archivesBaseName}-${project.version}.jar")
            } else {
                outputFile = new File("${project.buildDir}/libs/${project.archivesBaseName}-${project.version}-${classifier}.jar")
            }

            outputFiles.put(platform.name, outputFile)

            project.tasks.create(name: jarTaskName, type: Jar) { jarTask ->
                duplicatesStrategy = 'exclude'
                from {
                    project.tasks.getByName('shadowJar').archiveFile.map { shadowJarFile -> project.zipTree(shadowJarFile) }
                }

                from {
                     project.tasks.getByName('jarJar').archiveFile.map {
                        jarJarFile -> project.zipTree(jarJarFile).matching {
                            include "META-INF/jarjar/**"
                        }
                    }
                }

                from {
                    platform.dependencies.collect { dep ->
                        dep.resolve().collect { file ->
                            project.zipTree(file)
                        }
                    }
                }

                // 配置输出
                archiveFileName = outputFile.name
                destinationDirectory = outputFile.parentFile
                
                // 继承主项目的 manifest
                manifest {
                    attributes(project.tasks.jar.manifest.attributes)
                }
                dependsOn("reobfShadowJar")
            }

            // 将平台特定的 jar 任务设置为当前任务的依赖
            dependsOn(jarTaskName)
        }

        @TaskAction
        void apply() {}

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

        @OutputFiles
        getOutputFiles() {
            return outputFiles
        }
    }

    static class Platform {
        String name

        String archiveClassifier = null
        
        @Classpath
        public ArrayList<Configuration> dependencies;

        void name(String name) {
            this.name = name
        }

        void setArchiveClassifier(String archiveClassifier) {
            this.archiveClassifier = archiveClassifier
        }

        String getArchiveClassifier() {
            System.out.println("getArchiveClassifier: ${archiveClassifier} | ${name}")
            return archiveClassifier == null ? name : archiveClassifier
        }

        String getPlatfromName() {
            return name
        }
    }
}