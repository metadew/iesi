package io.metadew.iesi.connection;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.model.*;
import org.jfrog.artifactory.client.model.repository.settings.impl.GenericRepositorySettingsImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jfrog.artifactory.client.model.impl.RepositoryTypeImpl.LOCAL;

/**
 * Connection object for an Artifactory repository
 *
 * @author peter.billen
 */
public class ArtifactoryConnection {

    private String connectionURL = "";
    private String userName = "";
    private String userPassword = null;
    private String repositoryName = "";

    // Examples
    private static String fileNameToUpload = "ex_upload_1.txt";
    private static String fileUploadToLocation = "ex_fold1/ex_upload_1.txt";
    private static String fileDownloadToLocation = "ex_download_1.txt";

    // Constructor
    public ArtifactoryConnection() {

    }

    public ArtifactoryConnection(String connectionUrl, String userName, String userPassword) {
        super();
        this.setConnectionURL(connectionUrl);
        this.setUserName(userName);
        this.setUserPassword(userPassword);
    }

    public ArtifactoryConnection(String connectionUrl, String userName, String userPassword, String repositoryName) {
        super();
        this.setConnectionURL(connectionUrl);
        this.setUserName(userName);
        this.setUserPassword(userPassword);
        this.setRepositoryName(repositoryName);
    }

    public boolean downloadArtifact(String sourceFilePath, String targetFilePath) {
        Artifactory artifactory = ArtifactoryClientBuilder.create().setUrl(this.getConnectionURL())
                .setUsername(this.getUserName()).setPassword(this.getUserPassword()).build();
        InputStream iStream = artifactory.repository(this.getRepositoryName())
                .download(sourceFilePath).doDownload();

        java.io.File targetFile = new java.io.File(targetFilePath);

        try {
            java.nio.file.Files.copy(iStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(iStream);
        }
        return true;
    }

    public String getlatestBuild(String repositoryAssetPath, String buildPrefix) {
        Artifactory artifactory = ArtifactoryClientBuilder.create().setUrl(this.getConnectionURL())
                .setUsername(this.getUserName()).setPassword(this.getUserPassword()).build();

        Folder folder = artifactory.repository(this.getRepositoryName()).folder(repositoryAssetPath).info();

        List<Item> items = folder.getChildren();
        int resultVersion = -1;
        for (Item item : items) {
            if (item.isFolder()) {
                String tempVersionStripped = item.getName().substring(buildPrefix.length());
                int tempVersion = Integer.valueOf(tempVersionStripped);
                if (tempVersion > resultVersion) resultVersion = tempVersion;
            }
        }

        return buildPrefix + resultVersion;
    }

    @SuppressWarnings("unused")
    public void test() {
        Artifactory artifactory = ArtifactoryClientBuilder.create().setUrl(this.getConnectionURL())
                .setUsername(this.getUserName()).setPassword(this.getUserPassword()).build();

        Folder folder = artifactory.repository(this.getRepositoryName()).folder("folder").info();
        boolean isFolder = folder.isFolder();
        String repoName = folder.getRepo();
        String folderPath = folder.getPath();
        int childrenItemsSize = folder.getChildren().size();

        List<Item> items = folder.getChildren();
        for (Item item : items) {
            System.out.println(item.getName());
        }


    }

    @SuppressWarnings("unused")
    public void exampleMethods() throws Exception {
        //create artifactory object
        Artifactory artifactory = createArtifactory(this.getUserName(), this.getUserPassword(), this.getConnectionURL());

        if (artifactory == null) {
            throw new RuntimeException("artifactory creation failed");
        }

        //create repository
        String repositoryCreationResult = createNewRepository(artifactory, this.getRepositoryName());

        //create and upload a file
        File uploadedFile = uploadFile(artifactory, this.getRepositoryName(), fileUploadToLocation, fileNameToUpload);

        //search for file
        List<RepoPath> searchResult = searchFile(artifactory, this.getRepositoryName(), fileNameToUpload);

        //download file from artifactory
        java.io.File downloadedFile = downloadFile(artifactory, this.getRepositoryName(), fileUploadToLocation, fileDownloadToLocation);


        System.out.print("Example finished.");
    }

    /**
     * This method creates an artifactory object
     */
    private static Artifactory createArtifactory(String username, String password, String repoUrl) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(repoUrl)) {
            throw new IllegalArgumentException("Arguments passed to createArtifactory are not valid");
        }

        return ArtifactoryClientBuilder.create().setUrl(repoUrl).setUsername(username).setPassword(password).build();
    }

    /**
     * This method checks whether repository with supplied name exists or not, and
     * creates new if required.
     */
    private static String createNewRepository(Artifactory artifactory, String repoName) {
        if (artifactory == null || StringUtils.isEmpty(repoName)) {
            throw new IllegalArgumentException("Arguments passed to createNewRepository are not valid");
        }

        List<LightweightRepository> repoList = artifactory.repositories().list(LOCAL);
        Set<String> repoNamesList = repoList.stream().map(LightweightRepository::getKey).collect(Collectors.toSet());

        String creationResult = null;
        if (repoNamesList != null && !(repoNamesList.contains(repoName))) {
            GenericRepositorySettingsImpl settings = new GenericRepositorySettingsImpl();
            Repository repository = artifactory.repositories().builders().localRepositoryBuilder().key(repoName)
                    .description("new example local repository").repositorySettings(settings).build();
            creationResult = artifactory.repositories().create(1, repository);
        }

        return creationResult;
    }

    /**
     * This method receives the uploaded file source and destination, performs the
     * upload to artifactory
     */
    private static File uploadFile(Artifactory artifactory, String repo, String destPath, String fileNameToUpload)
            throws IOException {
        if (StringUtils.isEmpty(repo) || StringUtils.isEmpty(destPath) || StringUtils.isEmpty(fileNameToUpload)
                || artifactory == null) {
            throw new IllegalArgumentException("Arguments passed to createArtifactory are not valid");
        }

        Path path = Paths.get(fileNameToUpload);
        Files.write(path, Collections.singleton("This is an example line"), Charset.forName("UTF-8"));

        java.io.File file = new java.io.File(fileNameToUpload);

        return artifactory.repository(repo).upload(destPath, file).doUpload();
    }

    /**
     * Search for file by name in a specific repository, return the location of file
     */
    private static List<RepoPath> searchFile(Artifactory artifactory, String repoName, String fileToSearch) {
        if (artifactory == null || StringUtils.isEmpty(repoName) || StringUtils.isEmpty(fileToSearch)) {
            throw new IllegalArgumentException("Arguments passed to serachFile are not valid");
        }

        return artifactory.searches().repositories(repoName).artifactsByName(fileToSearch).doSearch();
    }

    /**
     * Download the required file from artifactory
     */
    private static java.io.File downloadFile(Artifactory artifactory, String repo, String filePath,
                                             String fileDownloadToLocation) throws Exception {
        if (artifactory == null || StringUtils.isEmpty(repo) || StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("Arguments passed to downloadFile are not valid");
        }

        InputStream inputStream = artifactory.repository(repo).download(filePath).doDownload();

        java.io.File targetFile = new java.io.File(fileDownloadToLocation);
        FileUtils.copyInputStreamToFile(inputStream, targetFile);

        return targetFile;
    }

    // Getters and setters
    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}