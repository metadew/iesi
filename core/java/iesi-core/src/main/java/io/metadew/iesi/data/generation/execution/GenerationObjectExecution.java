package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.data.generation.configuration.*;
import io.metadew.iesi.data.generation.tools.GenerationTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class GenerationObjectExecution {

    public static final String DEFAULT_LOCALE = "en";

    private Address address;
    private App app;
    private Avatar avatar;
    private Book book;
    private Bool bool;
    private Color color;
    private Company company;
    private CreditCard creditCard;
    private Date date;
    private Internet internet;
    private Lorem lorem;
    private Motd motd;
    private Name name;
    private io.metadew.iesi.data.generation.configuration.Number number;
    private Pattern pattern;
    private PhoneNumber phoneNumber;
    private Placeholdit placeholdit;
    private Retail retail;
    private SlackEmoji slackEmoji;
    private Team team;
    private Time time;
    private Timestamp timestamp;
    private University university;

    private String locale;
    private GenerationDataExecution execution;
    private GenerationTools generationTools;
    private FrameworkExecution frameworkExecution;

    public GenerationObjectExecution(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.initialize(DEFAULT_LOCALE);
    }

    private void initialize(String locale) {
        this.setGenerationTools(new GenerationTools());
        this.locale = locale;

        // Load execution
        Map<String, Object> data = this.loadConfiguration();
        this.execution = new GenerationDataExecution(data);

        // Load components
        this.setAddress(this.execution.getComponent(Address.class));
        this.setApp(this.execution.getComponent(App.class));
        this.setAvatar(this.execution.getComponent(Avatar.class));
        this.setBook(this.execution.getComponent(Book.class));
        this.setBool(this.execution.getComponent(Bool.class));
        this.setColor(this.execution.getComponent(Color.class));
        this.setCompany(this.execution.getComponent(Company.class));
        this.setCreditCard(this.execution.getComponent(CreditCard.class));
        this.setDate(this.execution.getComponent(Date.class));
        this.setInternet(this.execution.getComponent(Internet.class));
        this.setLorem(this.execution.getComponent(Lorem.class));
        this.setMotd(this.execution.getComponent(Motd.class));
        this.setName(this.execution.getComponent(Name.class));
        this.setNumber(this.execution
                .getComponent(io.metadew.iesi.data.generation.configuration.Number.class));
        this.setPattern(this.execution.getComponent(Pattern.class));
        this.setPhoneNumber(this.execution.getComponent(PhoneNumber.class));
        this.setPlaceholdit(this.execution.getComponent(Placeholdit.class));
        this.setRetail(this.execution.getComponent(Retail.class));
        this.setSlackEmoji(this.execution.getComponent(SlackEmoji.class));
        this.setTeam(this.execution.getComponent(Team.class));
        this.setTime(this.execution.getComponent(Time.class));
        this.setTimestamp(this.execution.getComponent(Timestamp.class));
        this.setUniversity(this.execution.getComponent(University.class));

    }

    private Map<String, Object> loadConfiguration() {
        Map<String, Object> data = null;

        int i = 0;
        // Default Configuration
        try {
            for (File file : FolderTools.getFilesInFolder(
                    this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.gen"),
                    "regex", ".+\\.yml")) {

                if (i == 0) {
                    data = loadData(file.getAbsolutePath());
                } else {
                    this.getGenerationTools().getMapTools().deepMerge(data, loadData(file.getAbsolutePath()));
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // User configuration
        try {
            for (File file : FolderTools.getFilesInFolder(
                    this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("metadata.conf"),
                    "regex", ".+\\.yml")) {

                if (i == 0) {
                    data = loadData(file.getAbsolutePath());
                } else {
                    this.getGenerationTools().getMapTools().deepMerge(data, loadData(file.getAbsolutePath()));
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> loadData(String fullFileName) {
        Yaml yaml = new Yaml();
        InputStream input = getDataInputStream(fullFileName);

        Map<String, Object> root = (Map<String, Object>) yaml.load(input);
        Map<String, Object> eoGenerationData = (Map<String, Object>) root.values().iterator().next();
        return (Map<String, Object>) eoGenerationData.values().iterator().next();
    }

    private InputStream getDataInputStream(String fullFileName) {
        // InputStream input =
        // getClass().getClassLoader().getResourceAsStream("locales/" + locale +
        // ".yml");
        InputStream input = FileTools
                .getInputStream(new File(fullFileName));

        try {
            assert input != null && input.available() != 0;
        } catch (AssertionError | IOException e) {
            throw new IllegalArgumentException("Unavailable locale \'" + locale + "\'");
        }

        return input;
    }

    // Getters and Setters
    public GenerationTools getGenerationTools() {
        return generationTools;
    }

    public void setGenerationTools(GenerationTools generationTools) {
        this.generationTools = generationTools;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public GenerationDataExecution getExecution() {
        return execution;
    }

    public void setExecution(GenerationDataExecution execution) {
        this.execution = execution;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setInternet(Internet internet) {
        this.internet = internet;
    }

    public Internet getInternet() {
        return internet;
    }

    public Lorem getLorem() {
        return lorem;
    }

    public void setLorem(Lorem lorem) {
        this.lorem = lorem;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public io.metadew.iesi.data.generation.configuration.Number getNumber() {
        return number;
    }

    public void setNumber(io.metadew.iesi.data.generation.configuration.Number number) {
        this.number = number;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Placeholdit getPlaceholdit() {
        return placeholdit;
    }

    public void setPlaceholdit(Placeholdit placeholdit) {
        this.placeholdit = placeholdit;
    }

    public Retail getRetail() {
        return retail;
    }

    public void setRetail(Retail retail) {
        this.retail = retail;
    }

    public SlackEmoji getSlackEmoji() {
        return slackEmoji;
    }

    public void setSlackEmoji(SlackEmoji slackEmoji) {
        this.slackEmoji = slackEmoji;
    }

    public Bool getBool() {
        return bool;
    }

    public void setBool(Bool bool) {
        this.bool = bool;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public Motd getMotd() {
        return motd;
    }

    public void setMotd(Motd motd) {
        this.motd = motd;
    }

}
