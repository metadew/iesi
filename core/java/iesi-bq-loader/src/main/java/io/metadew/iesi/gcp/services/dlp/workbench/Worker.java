package io.metadew.iesi.gcp.services.dlp.workbench;

import com.google.cloud.dlp.v2.DlpServiceClient;
import com.google.privacy.dlp.v2.*;

import java.io.IOException;
import java.util.Arrays;

public class Worker {

    public static void main(String[] args) throws Exception {
        String projectId = "iesi-01";
        String textToInspect = "My name is Alicia Abernathy, and my email address is aabernathy@example.com.";
        deIdentifyWithRedaction(projectId, textToInspect);
        deIdentifyWithReplacement(projectId, textToInspect);
        deIdentifyWithMasking(projectId, textToInspect);
    }

    // Inspects the provided text.
    public static void deIdentifyWithRedaction(String projectId, String textToRedact) {
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (DlpServiceClient dlp = DlpServiceClient.create()) {
            // Specify the content to be inspected.
            ContentItem item = ContentItem.newBuilder()
                    .setValue(textToRedact).build();

            // Specify the type of info the inspection will look for.
            // See https://cloud.google.com/dlp/docs/infotypes-reference for complete list of info types
            InfoType infoType = InfoType.newBuilder().setName("EMAIL_ADDRESS").build();
            InspectConfig inspectConfig = InspectConfig.newBuilder().addInfoTypes(infoType).build();
            // Define type of deidentification.
            PrimitiveTransformation primitiveTransformation = PrimitiveTransformation.newBuilder()
                    .setRedactConfig(RedactConfig.getDefaultInstance())
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
                            .setParent(LocationName.of(projectId, "global").toString())
                            .setItem(item)
                            .setDeidentifyConfig(redactConfig)
                            .setInspectConfig(inspectConfig)
                            .build();

            // Use the client to send the API request.
            DeidentifyContentResponse response = dlp.deidentifyContent(request);

            // Parse the response and process results
            System.out.println("Text after redaction: " + response.getItem().getValue());
        } catch (Exception e) {
            System.out.println("Error during inspectString: \n" + e.toString());
        }
    }

    public static void deIdentifyWithReplacement(String projectId, String textToRedact) {
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
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
                            .setParent(LocationName.of(projectId, "global").toString())
                            .setItem(item)
                            .setDeidentifyConfig(redactConfig)
                            .setInspectConfig(inspectConfig)
                            .build();

            // Use the client to send the API request.
            DeidentifyContentResponse response = dlp.deidentifyContent(request);

            // Parse the response and process results
            System.out.println("Text after redaction: " + response.getItem().getValue());
        } catch (Exception e) {
            System.out.println("Error during inspectString: \n" + e.toString());
        }
    }

    public static void deIdentifyWithMasking(String projectId, String textToDeIdentify)
            throws IOException {
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (DlpServiceClient dlp = DlpServiceClient.create()) {

            // Specify what content you want the service to DeIdentify
            ContentItem contentItem = ContentItem.newBuilder().setValue(textToDeIdentify).build();

            // Specify the type of info the inspection will look for.
            // See https://cloud.google.com/dlp/docs/infotypes-reference for complete list of info types
            InfoType infoType = InfoType.newBuilder().setName("EMAIL_ADDRESS").build();
            InspectConfig inspectConfig =
                    InspectConfig.newBuilder().addAllInfoTypes(Arrays.asList(infoType)).build();

            // Specify how the info from the inspection should be masked.
            CharacterMaskConfig characterMaskConfig =
                    CharacterMaskConfig.newBuilder()
                            .setMaskingCharacter("X") // Character to replace the found info with
                            .setNumberToMask(5) // How many characters should be masked
                            .build();
            PrimitiveTransformation primitiveTransformation =
                    PrimitiveTransformation.newBuilder()
                            .setCharacterMaskConfig(characterMaskConfig)
                            .build();
            InfoTypeTransformations.InfoTypeTransformation infoTypeTransformation =
                    InfoTypeTransformations.InfoTypeTransformation.newBuilder()
                            .setPrimitiveTransformation(primitiveTransformation)
                            .build();
            InfoTypeTransformations transformations =
                    InfoTypeTransformations.newBuilder().addTransformations(infoTypeTransformation).build();

            DeidentifyConfig deidentifyConfig =
                    DeidentifyConfig.newBuilder().setInfoTypeTransformations(transformations).build();

            // Combine configurations into a request for the service.
            DeidentifyContentRequest request =
                    DeidentifyContentRequest.newBuilder()
                            .setParent(LocationName.of(projectId, "global").toString())
                            .setItem(contentItem)
                            .setInspectConfig(inspectConfig)
                            .setDeidentifyConfig(deidentifyConfig)
                            .build();

            // Send the request and receive response from the service
            DeidentifyContentResponse response = dlp.deidentifyContent(request);

            // Print the results
            System.out.println("Text after masking: " + response.getItem().getValue());
        }
    }
}
