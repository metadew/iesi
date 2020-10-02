package io.metadew.iesi.gcp.services.dlp.common;

import com.google.cloud.dlp.v2.DlpServiceClient;
import com.google.privacy.dlp.v2.*;
import io.metadew.iesi.gcp.common.configuration.Spec;
import io.metadew.iesi.gcp.spec.dlp.DlpSpec;
import lombok.Getter;

@Getter
public class DlpService {
    private static DlpService INSTANCE;
    private DlpSpec dlpSpec;
    private String projectName;

    public synchronized static DlpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DlpService();
        }
        return INSTANCE;
    }

    private DlpService () {

    }
    public void init (String projectName, String dlpName) {
        this.projectName = projectName;
        dlpSpec = null;
        for (DlpSpec entry : Spec.getInstance().getGcpSpec().getDlp()) {
            if (entry.getName().equalsIgnoreCase(dlpName)) {
                dlpSpec = entry;
            }
        }
    }

    public String deIdentifyWithReplacement(String textToRedact) {
        String output = "";
        try (DlpServiceClient dlp = DlpServiceClient.create()) {
            // Specify the content to be inspected.
            ContentItem item = ContentItem.newBuilder()
                    .setValue(textToRedact).build();

            // Specify the type of info the inspection will look for.
            // See https://cloud.google.com/dlp/docs/infotypes-reference for complete list of info types
            InfoType infoType = InfoType.newBuilder().setName("EMAIL_ADDRESS").build();
            InspectConfig inspectConfig = InspectConfig.newBuilder().addInfoTypes(infoType).build();
            // Specify replacement string to be used for the finding.
            ReplaceValueConfig replaceValueConfig = ReplaceValueConfig.newBuilder()
                    .setNewValue(Value.newBuilder().setStringValue("[EMAIL_ADDRESS]").build())
                    .build();
            // Define type of deidentification as replacement.
            PrimitiveTransformation primitiveTransformation = PrimitiveTransformation.newBuilder()
                    .setReplaceConfig(replaceValueConfig)
                    .build();
            // Associate deidentification type with info type.
            InfoTypeTransformations.InfoTypeTransformation transformation = InfoTypeTransformations.InfoTypeTransformation.newBuilder()
                    .addInfoTypes(infoType)
                    .setPrimitiveTransformation(primitiveTransformation)
                    .build();
            // Construct the configuration for the Redact request and list all desired transformations.
            DeidentifyConfig redactConfig = DeidentifyConfig.newBuilder()
                    .setInfoTypeTransformations(InfoTypeTransformations.newBuilder()
                            .addTransformations(transformation))
                    .build();

            // Construct the Redact request to be sent by the client.
            DeidentifyContentRequest request =
                    DeidentifyContentRequest.newBuilder()
                            .setParent(LocationName.of(projectName, "global").toString())
                            .setItem(item)
                            .setDeidentifyConfig(redactConfig)
                            .setInspectConfig(inspectConfig)
                            .build();

            // Use the client to send the API request.
            DeidentifyContentResponse response = dlp.deidentifyContent(request);

            // Parse the response and process results
            output = response.getItem().getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


}
