package io.metadew.iesi.assembly.execution;

import io.metadew.iesi.connection.tools.FolderTools;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Comparator;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Log4j2
public class AssemblyService {

    private String repository;
    private String sandbox;
    private String instance;
    private String version;


    public AssemblyService(String repository, String sandbox, String instance, String version) {
        this.repository = repository;
        this.sandbox = sandbox;
        this.instance = instance;
        this.version = version;
    }

    // Methods
    public void execute() throws IOException {
        Path versionHomePath = Paths.get(sandbox, version, instance);

        deleteVersionDirectory(versionHomePath);
        recreateVersionDirectory(versionHomePath);
        createIESISkeleton(versionHomePath);
        loadLicenses(versionHomePath);
        loadMavenDependencies(versionHomePath);
        loadRestLicenses(versionHomePath);
        //loadRestDependencies(versionHomePath);
        loadAssets(versionHomePath);
    }

    private void loadAssets(Path versionHome) throws IOException {
        log.info(MessageFormat.format("Loading assets into version home: {0}", versionHome));
        // Load assets into directory structure
        String fileSystemConfig = repository + File.separator + "core" + File.separator + "assembly" + File.separator + "file-assembly.conf";
        File file = new File(fileSystemConfig);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readLine;
        while ((readLine = bufferedReader.readLine()) != null) {
            String innerpart = readLine.trim();
            String[] parts = innerpart.split(";");
            // int assemblyCode = Integer.parseInt(parts[4]);

            // Take all items into account
            String sourcePath = parts[1].replace("#GIT_REPO#", repository);
            sourcePath = sourcePath.replace("#VERSION#", version);
            String targetPath = versionHome.toString() + parts[2] + File.separator + parts[0];
            log.info("Copying " + sourcePath + " to " + targetPath);
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath));
            //FileTools.copyFromFileToFile(sourcePath, targetPath);
        }
        bufferedReader.close();
    }

    private void loadRestDependencies(Path versionHome) throws IOException {
        log.info(MessageFormat.format("Loading dependencies (REST) into version home: {0}", versionHome));
        String mavenDependenciesSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-rest-without-microservices" + File.separator + "target";
        String mavenDependenciesTarget = versionHome.toString() + File.separator + "rest";
        Path restJar = Files.walk(Paths.get(mavenDependenciesSource), 1)
                .filter(path -> path.getFileName().toString().endsWith("jar"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find REST jar"));
        Files.copy(restJar, Paths.get(mavenDependenciesTarget).resolve(restJar.getFileName()), REPLACE_EXISTING);
    }

    private void loadRestLicenses(Path versionHome) {
        log.info(MessageFormat.format("Loading licenses (REST) into version home: {0}", versionHome));
        String licensesReportSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-rest-without-microservices" + File.separator + "target" + File.separator + "site";
        String licensesReportTarget = versionHome.toString() + File.separator + "licenses" + File.separator + "rest";
        FolderTools.copyFromFolderToFolder(licensesReportSource, licensesReportTarget, true);
    }

    private void loadMavenDependencies(Path versionHome) {
        log.info(MessageFormat.format("Loading rest into version home: {0}", versionHome));
        String mavenDependenciesSource = repository + File.separator + "core" + File.separator + "java" + File.separator + "iesi-core" + File.separator + "target" + File.separator + "dependencies";
        String mavenDependenciesTarget = versionHome.toString() + File.separator + "lib";
        FolderTools.copyFromFolderToFolder(mavenDependenciesSource, mavenDependenciesTarget, true);
    }

    private void loadLicenses(Path versionHome) {
        log.info(MessageFormat.format("Loading licenses into version home: {0}", versionHome));
        String licensesSource = repository + File.separator + "licenses";
        String licensesTarget = versionHome.toString() + File.separator + "licenses";
        FolderTools.copyFromFolderToFolder(licensesSource, licensesTarget, true);

        String licensesReportSource = repository + File.separator + "core" + File.separator + "java"
                + File.separator + "iesi-core" + File.separator + "target" + File.separator + "site";
        String licensesReportTarget = versionHome.toString() + File.separator + "licenses" + File.separator + "core";
        FolderTools.copyFromFolderToFolder(licensesReportSource, licensesReportTarget, true);
    }

    private void createIESISkeleton(Path versionHome) {
        log.info(MessageFormat.format("Creating IESI Skeleton at version home: {0}", versionHome));
        Path configurationFilePath = Paths.get(repository, "core", "assembly", "folder-assembly.conf");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(configurationFilePath.toFile()));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                String innerpart = readLine.trim();
                String[] parts = innerpart.split(";");
                log.info("creating folder " + versionHome.resolve(parts[0]));
                Files.createDirectories(versionHome.resolve(parts[0]));
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException("Issue creating solution structure", e);
        }
    }

    private void recreateVersionDirectory(Path versionHomePath) throws IOException {
        log.info(MessageFormat.format("Recreating version home: {0}", versionHomePath));
        Files.createDirectories(versionHomePath);
    }

    private void deleteVersionDirectory(Path versionHomePath) throws IOException {
        if (Files.exists(versionHomePath)) {
            log.info(MessageFormat.format("Deleting version home: {0}", versionHomePath));
            Files.walk(versionHomePath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

}