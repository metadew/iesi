package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenAPIGeneratorTest {

    byte[] docFile;
    byte[] wrongDocFile;
    String title;
    String version;

    @BeforeEach
    public void init() {
        String base64 = "b3BlbmFwaTogMy4wLjINCmluZm86DQogIHRpdGxlOiBTd2FnZ2VyIFBldHN0b3JlIC0gT3BlbkFQSSAzLjANCiAgZGVzY3JpcHRpb246IHNtYWxsIGRlc2NyaXB0aW9uDQogIHRlcm1zT2ZTZXJ2aWNlOiBodHRwOi8vc3dhZ2dlci5pby90ZXJtcy8NCiAgY29udGFjdDoNCiAgICBlbWFpbDogYXBpdGVhbUBzd2FnZ2VyLmlvDQogIGxpY2Vuc2U6DQogICAgbmFtZTogQXBhY2hlIDIuMA0KICAgIHVybDogaHR0cDovL3d3dy5hcGFjaGUub3JnL2xpY2Vuc2VzL0xJQ0VOU0UtMi4wLmh0bWwNCiAgdmVyc2lvbjogMQ0KZXh0ZXJuYWxEb2NzOg0KICBkZXNjcmlwdGlvbjogRmluZCBvdXQgbW9yZSBhYm91dCBTd2FnZ2VyDQogIHVybDogaHR0cDovL3N3YWdnZXIuaW8NCnNlcnZlcnM6DQogIC0gdXJsOiAiaHR0cHM6Ly9wZXRzdG9yZTMuc3dhZ2dlci5pby9hcGkvdjMvIg0KdGFnczoNCiAgLSBuYW1lOiBwZXQNCiAgICBkZXNjcmlwdGlvbjogRXZlcnl0aGluZyBhYm91dCB5b3VyIFBldHMNCiAgICBleHRlcm5hbERvY3M6DQogICAgICBkZXNjcmlwdGlvbjogRmluZCBvdXQgbW9yZQ0KICAgICAgdXJsOiBodHRwOi8vc3dhZ2dlci5pbw0KICAtIG5hbWU6IHN0b3JlDQogICAgZGVzY3JpcHRpb246IE9wZXJhdGlvbnMgYWJvdXQgdXNlcg0KICAtIG5hbWU6IHVzZXINCiAgICBkZXNjcmlwdGlvbjogQWNjZXNzIHRvIFBldHN0b3JlIG9yZGVycw0KICAgIGV4dGVybmFsRG9jczoNCiAgICAgIGRlc2NyaXB0aW9uOiBGaW5kIG91dCBtb3JlIGFib3V0IG91ciBzdG9yZQ0KICAgICAgdXJsOiBodHRwOi8vc3dhZ2dlci5pbw0KcGF0aHM6DQogICIvcGV0IjoNCiAgICBwdXQ6DQogICAgICB0YWdzOg0KICAgICAgICAtIHBldA0KICAgICAgc3VtbWFyeTogVXBkYXRlIGFuIGV4aXN0aW5nIHBldA0KICAgICAgZGVzY3JpcHRpb246IFVwZGF0ZSBhbiBleGlzdGluZyBwZXQgYnkgSWQNCiAgICAgIG9wZXJhdGlvbklkOiB1cGRhdGVQZXQNCiAgICAgIHJlcXVlc3RCb2R5Og0KICAgICAgICBkZXNjcmlwdGlvbjogVXBkYXRlIGFuIGV4aXN0ZW50IHBldCBpbiB0aGUgc3RvcmUNCiAgICAgICAgY29udGVudDoNCiAgICAgICAgICBhcHBsaWNhdGlvbi9qc29uOg0KICAgICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgICAgYXBwbGljYXRpb24veG1sOg0KICAgICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgICAgYXBwbGljYXRpb24veC13d3ctZm9ybS11cmxlbmNvZGVkOg0KICAgICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgIHJlcXVpcmVkOiB0cnVlDQogICAgICByZXNwb25zZXM6DQogICAgICAgICcyMDAnOg0KICAgICAgICAgIGRlc2NyaXB0aW9uOiBTdWNjZXNzZnVsIG9wZXJhdGlvbg0KICAgICAgICAgIGNvbnRlbnQ6DQogICAgICAgICAgICBhcHBsaWNhdGlvbi94bWw6DQogICAgICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgICAgICBhcHBsaWNhdGlvbi9qc29uOg0KICAgICAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0Ig0KICAgICAgICAnNDAwJzoNCiAgICAgICAgICBkZXNjcmlwdGlvbjogSW52YWxpZCBJRCBzdXBwbGllZA0KICAgICAgICAnNDA0JzoNCiAgICAgICAgICBkZXNjcmlwdGlvbjogUGV0IG5vdCBmb3VuZA0KICAgICAgICAnNDA1JzoNCiAgICAgICAgICBkZXNjcmlwdGlvbjogVmFsaWRhdGlvbiBleGNlcHRpb24NCiAgICAgIHNlY3VyaXR5Og0KICAgICAgICAtIHBldHN0b3JlX2F1dGg6DQogICAgICAgICAgICAtIHdyaXRlOnBldHMNCiAgICAgICAgICAgIC0gcmVhZDpwZXRzDQoNCmNvbXBvbmVudHM6DQogIHNjaGVtYXM6DQogICAgT3JkZXI6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTANCiAgICAgICAgcGV0SWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxOTg3NzINCiAgICAgICAgcXVhbnRpdHk6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50MzINCiAgICAgICAgICBleGFtcGxlOiA3DQogICAgICAgIHNoaXBEYXRlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGZvcm1hdDogZGF0ZS10aW1lDQogICAgICAgIHN0YXR1czoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBkZXNjcmlwdGlvbjogT3JkZXIgU3RhdHVzDQogICAgICAgICAgZXhhbXBsZTogYXBwcm92ZWQNCiAgICAgICAgICBlbnVtOg0KICAgICAgICAgICAgLSBwbGFjZWQNCiAgICAgICAgICAgIC0gYXBwcm92ZWQNCiAgICAgICAgICAgIC0gZGVsaXZlcmVkDQogICAgICAgIGNvbXBsZXRlOg0KICAgICAgICAgIHR5cGU6IGJvb2xlYW4NCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogb3JkZXINCiAgICBDdXN0b21lcjoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxMDAwMDANCiAgICAgICAgdXNlcm5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogZmVoZ3V5DQogICAgICAgIGFkZHJlc3M6DQogICAgICAgICAgdHlwZTogYXJyYXkNCiAgICAgICAgICB4bWw6DQogICAgICAgICAgICBuYW1lOiBhZGRyZXNzZXMNCiAgICAgICAgICAgIHdyYXBwZWQ6IHRydWUNCiAgICAgICAgICBpdGVtczoNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL0FkZHJlc3MiDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IGN1c3RvbWVyDQogICAgQWRkcmVzczoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgc3RyZWV0Og0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IDQzNyBMeXR0b24NCiAgICAgICAgY2l0eToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBQYWxvIEFsdG8NCiAgICAgICAgc3RhdGU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogQ0ENCiAgICAgICAgemlwOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6ICc5NDMwMScNCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogYWRkcmVzcw0KICAgIENhdGVnb3J5Og0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICAgIGV4YW1wbGU6IDENCiAgICAgICAgbmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBEb2dzDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IGNhdGVnb3J5DQogICAgVXNlcjoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxMA0KICAgICAgICB1c2VybmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiB0aGVVc2VyDQogICAgICAgIGZpcnN0TmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBKb2huDQogICAgICAgIGxhc3ROYW1lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IEphbWVzDQogICAgICAgIGVtYWlsOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IGpvaG5AZW1haWwuY29tDQogICAgICAgIHBhc3N3b3JkOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6ICcxMjM0NScNCiAgICAgICAgcGhvbmU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogJzEyMzQ1Jw0KICAgICAgICB1c2VyU3RhdHVzOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBkZXNjcmlwdGlvbjogVXNlciBTdGF0dXMNCiAgICAgICAgICBmb3JtYXQ6IGludDMyDQogICAgICAgICAgZXhhbXBsZTogMQ0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiB1c2VyDQogICAgVGFnOg0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICBuYW1lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiB0YWcNCiAgICBQZXQ6DQogICAgICByZXF1aXJlZDoNCiAgICAgICAgLSBuYW1lDQogICAgICAgIC0gcGhvdG9VcmxzDQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTANCiAgICAgICAgbmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBkb2dnaWUNCiAgICAgICAgY2F0ZWdvcnk6DQogICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvQ2F0ZWdvcnkiDQogICAgICAgIHBob3RvVXJsczoNCiAgICAgICAgICB0eXBlOiBhcnJheQ0KICAgICAgICAgIHhtbDoNCiAgICAgICAgICAgIHdyYXBwZWQ6IHRydWUNCiAgICAgICAgICBpdGVtczoNCiAgICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgICAgeG1sOg0KICAgICAgICAgICAgICBuYW1lOiBwaG90b1VybA0KICAgICAgICB0YWdzOg0KICAgICAgICAgIHR5cGU6IGFycmF5DQogICAgICAgICAgeG1sOg0KICAgICAgICAgICAgd3JhcHBlZDogdHJ1ZQ0KICAgICAgICAgIGl0ZW1zOg0KICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvVGFnIg0KICAgICAgICBzdGF0dXM6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZGVzY3JpcHRpb246IHBldCBzdGF0dXMgaW4gdGhlIHN0b3JlDQogICAgICAgICAgZW51bToNCiAgICAgICAgICAgIC0gYXZhaWxhYmxlDQogICAgICAgICAgICAtIHBlbmRpbmcNCiAgICAgICAgICAgIC0gc29sZA0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiBwZXQNCiAgICBBcGlSZXNwb25zZToNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgY29kZToNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQzMg0KICAgICAgICB0eXBlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICBtZXNzYWdlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiAiIyNkZWZhdWx0Ig0KICByZXF1ZXN0Qm9kaWVzOg0KICAgIFBldDoNCiAgICAgIGRlc2NyaXB0aW9uOiBQZXQgb2JqZWN0IHRoYXQgbmVlZHMgdG8gYmUgYWRkZWQgdG8gdGhlIHN0b3JlDQogICAgICBjb250ZW50Og0KICAgICAgICBhcHBsaWNhdGlvbi9qc29uOg0KICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgYXBwbGljYXRpb24veG1sOg0KICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICBVc2VyQXJyYXk6DQogICAgICBkZXNjcmlwdGlvbjogTGlzdCBvZiB1c2VyIG9iamVjdA0KICAgICAgY29udGVudDoNCiAgICAgICAgYXBwbGljYXRpb24vanNvbjoNCiAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICB0eXBlOiBhcnJheQ0KICAgICAgICAgICAgaXRlbXM6DQogICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1VzZXIiDQogIHNlY3VyaXR5U2NoZW1lczoNCiAgICBwZXRzdG9yZV9hdXRoOg0KICAgICAgdHlwZTogb2F1dGgyDQogICAgICBmbG93czoNCiAgICAgICAgaW1wbGljaXQ6DQogICAgICAgICAgYXV0aG9yaXphdGlvblVybDogaHR0cHM6Ly9wZXRzdG9yZTMuc3dhZ2dlci5pby9vYXV0aC9hdXRob3JpemUNCiAgICAgICAgICBzY29wZXM6DQogICAgICAgICAgICB3cml0ZTpwZXRzOiBtb2RpZnkgcGV0cyBpbiB5b3VyIGFjY291bnQNCiAgICAgICAgICAgIHJlYWQ6cGV0czogcmVhZCB5b3VyIHBldHMNCiAgICBhcGlfa2V5Og0KICAgICAgdHlwZTogYXBpS2V5DQogICAgICBuYW1lOiBhcGlfa2V5DQogICAgICBpbjogaGVhZGVy";
        String wrongBase64 = "b3BlbmFwaTogMy4wLjINCmluZm86DQogIGRlc2NyaXB0aW9uOiBzbWFsbCBkZXNjcmlwdGlvbg0KICB0ZXJtc09mU2VydmljZTogaHR0cDovL3N3YWdnZXIuaW8vdGVybXMvDQogIGNvbnRhY3Q6DQogICAgZW1haWw6IGFwaXRlYW1Ac3dhZ2dlci5pbw0KICBsaWNlbnNlOg0KICAgIG5hbWU6IEFwYWNoZSAyLjANCiAgICB1cmw6IGh0dHA6Ly93d3cuYXBhY2hlLm9yZy9saWNlbnNlcy9MSUNFTlNFLTIuMC5odG1sDQogIHZlcnNpb246IDENCmV4dGVybmFsRG9jczoNCiAgZGVzY3JpcHRpb246IEZpbmQgb3V0IG1vcmUgYWJvdXQgU3dhZ2dlcg0KICB1cmw6IGh0dHA6Ly9zd2FnZ2VyLmlvDQpzZXJ2ZXJzOg0KICAtIHVybDogImh0dHBzOi8vcGV0c3RvcmUzLnN3YWdnZXIuaW8vYXBpL3YzLyINCnRhZ3M6DQogIC0gbmFtZTogcGV0DQogICAgZGVzY3JpcHRpb246IEV2ZXJ5dGhpbmcgYWJvdXQgeW91ciBQZXRzDQogICAgZXh0ZXJuYWxEb2NzOg0KICAgICAgZGVzY3JpcHRpb246IEZpbmQgb3V0IG1vcmUNCiAgICAgIHVybDogaHR0cDovL3N3YWdnZXIuaW8NCiAgLSBuYW1lOiBzdG9yZQ0KICAgIGRlc2NyaXB0aW9uOiBPcGVyYXRpb25zIGFib3V0IHVzZXINCiAgLSBuYW1lOiB1c2VyDQogICAgZGVzY3JpcHRpb246IEFjY2VzcyB0byBQZXRzdG9yZSBvcmRlcnMNCiAgICBleHRlcm5hbERvY3M6DQogICAgICBkZXNjcmlwdGlvbjogRmluZCBvdXQgbW9yZSBhYm91dCBvdXIgc3RvcmUNCiAgICAgIHVybDogaHR0cDovL3N3YWdnZXIuaW8NCnBhdGhzOg0KICAiL3BldCI6DQogICAgcHV0Og0KICAgICAgdGFnczoNCiAgICAgICAgLSBwZXQNCiAgICAgIHN1bW1hcnk6IFVwZGF0ZSBhbiBleGlzdGluZyBwZXQNCiAgICAgIGRlc2NyaXB0aW9uOiBVcGRhdGUgYW4gZXhpc3RpbmcgcGV0IGJ5IElkDQogICAgICBvcGVyYXRpb25JZDogdXBkYXRlUGV0DQogICAgICByZXF1ZXN0Qm9keToNCiAgICAgICAgZGVzY3JpcHRpb246IFVwZGF0ZSBhbiBleGlzdGVudCBwZXQgaW4gdGhlIHN0b3JlDQogICAgICAgIGNvbnRlbnQ6DQogICAgICAgICAgYXBwbGljYXRpb24vanNvbjoNCiAgICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0Ig0KICAgICAgICAgIGFwcGxpY2F0aW9uL3htbDoNCiAgICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0Ig0KICAgICAgICAgIGFwcGxpY2F0aW9uL3gtd3d3LWZvcm0tdXJsZW5jb2RlZDoNCiAgICAgICAgICAgIHNjaGVtYToNCiAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0Ig0KICAgICAgICByZXF1aXJlZDogdHJ1ZQ0KICAgICAgcmVzcG9uc2VzOg0KICAgICAgICAnMjAwJzoNCiAgICAgICAgICBkZXNjcmlwdGlvbjogU3VjY2Vzc2Z1bCBvcGVyYXRpb24NCiAgICAgICAgICBjb250ZW50Og0KICAgICAgICAgICAgYXBwbGljYXRpb24veG1sOg0KICAgICAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAgICAgIiRyZWYiOiAiIy9jb21wb25lbnRzL3NjaGVtYXMvUGV0Ig0KICAgICAgICAgICAgYXBwbGljYXRpb24vanNvbjoNCiAgICAgICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1BldCINCiAgICAgICAgJzQwMCc6DQogICAgICAgICAgZGVzY3JpcHRpb246IEludmFsaWQgSUQgc3VwcGxpZWQNCiAgICAgICAgJzQwNCc6DQogICAgICAgICAgZGVzY3JpcHRpb246IFBldCBub3QgZm91bmQNCiAgICAgICAgJzQwNSc6DQogICAgICAgICAgZGVzY3JpcHRpb246IFZhbGlkYXRpb24gZXhjZXB0aW9uDQogICAgICBzZWN1cml0eToNCiAgICAgICAgLSBwZXRzdG9yZV9hdXRoOg0KICAgICAgICAgICAgLSB3cml0ZTpwZXRzDQogICAgICAgICAgICAtIHJlYWQ6cGV0cw0KDQpjb21wb25lbnRzOg0KICBzY2hlbWFzOg0KICAgIE9yZGVyOg0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICAgIGV4YW1wbGU6IDEwDQogICAgICAgIHBldElkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTk4NzcyDQogICAgICAgIHF1YW50aXR5Og0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDMyDQogICAgICAgICAgZXhhbXBsZTogNw0KICAgICAgICBzaGlwRGF0ZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBmb3JtYXQ6IGRhdGUtdGltZQ0KICAgICAgICBzdGF0dXM6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZGVzY3JpcHRpb246IE9yZGVyIFN0YXR1cw0KICAgICAgICAgIGV4YW1wbGU6IGFwcHJvdmVkDQogICAgICAgICAgZW51bToNCiAgICAgICAgICAgIC0gcGxhY2VkDQogICAgICAgICAgICAtIGFwcHJvdmVkDQogICAgICAgICAgICAtIGRlbGl2ZXJlZA0KICAgICAgICBjb21wbGV0ZToNCiAgICAgICAgICB0eXBlOiBib29sZWFuDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IG9yZGVyDQogICAgQ3VzdG9tZXI6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTAwMDAwDQogICAgICAgIHVzZXJuYW1lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IGZlaGd1eQ0KICAgICAgICBhZGRyZXNzOg0KICAgICAgICAgIHR5cGU6IGFycmF5DQogICAgICAgICAgeG1sOg0KICAgICAgICAgICAgbmFtZTogYWRkcmVzc2VzDQogICAgICAgICAgICB3cmFwcGVkOiB0cnVlDQogICAgICAgICAgaXRlbXM6DQogICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9BZGRyZXNzIg0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiBjdXN0b21lcg0KICAgIEFkZHJlc3M6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIHN0cmVldDoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiA0MzcgTHl0dG9uDQogICAgICAgIGNpdHk6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogUGFsbyBBbHRvDQogICAgICAgIHN0YXRlOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6IENBDQogICAgICAgIHppcDoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiAnOTQzMDEnDQogICAgICB4bWw6DQogICAgICAgIG5hbWU6IGFkZHJlc3MNCiAgICBDYXRlZ29yeToNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgICBleGFtcGxlOiAxDQogICAgICAgIG5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogRG9ncw0KICAgICAgeG1sOg0KICAgICAgICBuYW1lOiBjYXRlZ29yeQ0KICAgIFVzZXI6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGlkOg0KICAgICAgICAgIHR5cGU6IGludGVnZXINCiAgICAgICAgICBmb3JtYXQ6IGludDY0DQogICAgICAgICAgZXhhbXBsZTogMTANCiAgICAgICAgdXNlcm5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogdGhlVXNlcg0KICAgICAgICBmaXJzdE5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogSm9obg0KICAgICAgICBsYXN0TmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBKYW1lcw0KICAgICAgICBlbWFpbDoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiBqb2huQGVtYWlsLmNvbQ0KICAgICAgICBwYXNzd29yZDoNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICBleGFtcGxlOiAnMTIzNDUnDQogICAgICAgIHBob25lOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGV4YW1wbGU6ICcxMjM0NScNCiAgICAgICAgdXNlclN0YXR1czoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZGVzY3JpcHRpb246IFVzZXIgU3RhdHVzDQogICAgICAgICAgZm9ybWF0OiBpbnQzMg0KICAgICAgICAgIGV4YW1wbGU6IDENCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogdXNlcg0KICAgIFRhZzoNCiAgICAgIHR5cGU6IG9iamVjdA0KICAgICAgcHJvcGVydGllczoNCiAgICAgICAgaWQ6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50NjQNCiAgICAgICAgbmFtZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogdGFnDQogICAgUGV0Og0KICAgICAgcmVxdWlyZWQ6DQogICAgICAgIC0gbmFtZQ0KICAgICAgICAtIHBob3RvVXJscw0KICAgICAgdHlwZTogb2JqZWN0DQogICAgICBwcm9wZXJ0aWVzOg0KICAgICAgICBpZDoNCiAgICAgICAgICB0eXBlOiBpbnRlZ2VyDQogICAgICAgICAgZm9ybWF0OiBpbnQ2NA0KICAgICAgICAgIGV4YW1wbGU6IDEwDQogICAgICAgIG5hbWU6DQogICAgICAgICAgdHlwZTogc3RyaW5nDQogICAgICAgICAgZXhhbXBsZTogZG9nZ2llDQogICAgICAgIGNhdGVnb3J5Og0KICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL0NhdGVnb3J5Ig0KICAgICAgICBwaG90b1VybHM6DQogICAgICAgICAgdHlwZTogYXJyYXkNCiAgICAgICAgICB4bWw6DQogICAgICAgICAgICB3cmFwcGVkOiB0cnVlDQogICAgICAgICAgaXRlbXM6DQogICAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgICAgIHhtbDoNCiAgICAgICAgICAgICAgbmFtZTogcGhvdG9VcmwNCiAgICAgICAgdGFnczoNCiAgICAgICAgICB0eXBlOiBhcnJheQ0KICAgICAgICAgIHhtbDoNCiAgICAgICAgICAgIHdyYXBwZWQ6IHRydWUNCiAgICAgICAgICBpdGVtczoNCiAgICAgICAgICAgICIkcmVmIjogIiMvY29tcG9uZW50cy9zY2hlbWFzL1RhZyINCiAgICAgICAgc3RhdHVzOg0KICAgICAgICAgIHR5cGU6IHN0cmluZw0KICAgICAgICAgIGRlc2NyaXB0aW9uOiBwZXQgc3RhdHVzIGluIHRoZSBzdG9yZQ0KICAgICAgICAgIGVudW06DQogICAgICAgICAgICAtIGF2YWlsYWJsZQ0KICAgICAgICAgICAgLSBwZW5kaW5nDQogICAgICAgICAgICAtIHNvbGQNCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogcGV0DQogICAgQXBpUmVzcG9uc2U6DQogICAgICB0eXBlOiBvYmplY3QNCiAgICAgIHByb3BlcnRpZXM6DQogICAgICAgIGNvZGU6DQogICAgICAgICAgdHlwZTogaW50ZWdlcg0KICAgICAgICAgIGZvcm1hdDogaW50MzINCiAgICAgICAgdHlwZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgICAgbWVzc2FnZToNCiAgICAgICAgICB0eXBlOiBzdHJpbmcNCiAgICAgIHhtbDoNCiAgICAgICAgbmFtZTogIiMjZGVmYXVsdCINCiAgcmVxdWVzdEJvZGllczoNCiAgICBQZXQ6DQogICAgICBkZXNjcmlwdGlvbjogUGV0IG9iamVjdCB0aGF0IG5lZWRzIHRvIGJlIGFkZGVkIHRvIHRoZSBzdG9yZQ0KICAgICAgY29udGVudDoNCiAgICAgICAgYXBwbGljYXRpb24vanNvbjoNCiAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgICAgIGFwcGxpY2F0aW9uL3htbDoNCiAgICAgICAgICBzY2hlbWE6DQogICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9QZXQiDQogICAgVXNlckFycmF5Og0KICAgICAgZGVzY3JpcHRpb246IExpc3Qgb2YgdXNlciBvYmplY3QNCiAgICAgIGNvbnRlbnQ6DQogICAgICAgIGFwcGxpY2F0aW9uL2pzb246DQogICAgICAgICAgc2NoZW1hOg0KICAgICAgICAgICAgdHlwZTogYXJyYXkNCiAgICAgICAgICAgIGl0ZW1zOg0KICAgICAgICAgICAgICAiJHJlZiI6ICIjL2NvbXBvbmVudHMvc2NoZW1hcy9Vc2VyIg0KICBzZWN1cml0eVNjaGVtZXM6DQogICAgcGV0c3RvcmVfYXV0aDoNCiAgICAgIHR5cGU6IG9hdXRoMg0KICAgICAgZmxvd3M6DQogICAgICAgIGltcGxpY2l0Og0KICAgICAgICAgIGF1dGhvcml6YXRpb25Vcmw6IGh0dHBzOi8vcGV0c3RvcmUzLnN3YWdnZXIuaW8vb2F1dGgvYXV0aG9yaXplDQogICAgICAgICAgc2NvcGVzOg0KICAgICAgICAgICAgd3JpdGU6cGV0czogbW9kaWZ5IHBldHMgaW4geW91ciBhY2NvdW50DQogICAgICAgICAgICByZWFkOnBldHM6IHJlYWQgeW91ciBwZXRzDQogICAgYXBpX2tleToNCiAgICAgIHR5cGU6IGFwaUtleQ0KICAgICAgbmFtZTogYXBpX2tleQ0KICAgICAgaW46IGhlYWRlcg==";
        docFile = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        wrongDocFile = Base64.getDecoder().decode(wrongBase64.getBytes(StandardCharsets.UTF_8));
        title = "Swagger Petstore - OpenAPI 3.0";
        version = "1";
    }


    @Test
    void transformFromFile() throws IOException {
        File file = File.createTempFile("doc", null);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(docFile);
        }

        assertThat(OpenAPIGenerator.getInstance().transformFromFile(file.getPath())).isEqualTo(getTransformResult());
    }

    @Test
    void transformFromWrongFile() throws IOException {
        File file = File.createTempFile("doc", null);
        List<String> messages = Collections.singletonList("attribute info.title is missing");
        String filePath = file.getPath();
        OpenAPIGenerator openAPIGenerator = OpenAPIGenerator.getInstance();
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(wrongDocFile);
        }

        SwaggerParserException exception = assertThrows(SwaggerParserException.class, () -> openAPIGenerator.transformFromFile(filePath));
        assertThat(exception.getMessages()).isEqualTo(messages);
    }


    private TransformResult getTransformResult() {
        EnvironmentKey environmentKey = new EnvironmentKey("env0");
        ConnectionKey connectionKey = new ConnectionKey(
                "Swagger Petstore - OpenAPI 3.0",
                environmentKey);
        ComponentKey componentKey = new ComponentKey(
                IdentifierTools.getComponentIdentifier("updatePet"),
                1L);
        ComponentVersionKey componentVersionKey = new ComponentVersionKey(componentKey);
        ConnectionParameter host = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "host"),
                "petstore3.swagger.io");
        ConnectionParameter baseUrl = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "baseUrl"),
                "/api/v3/");
        ConnectionParameter tls = new ConnectionParameter(
                new ConnectionParameterKey(connectionKey, "tls"),
                "Y");
        Connection connection = new Connection(connectionKey, "http", "small description", Arrays.asList(baseUrl, host, tls));


        ComponentParameter endpoint = new ComponentParameter(
                new ComponentParameterKey(componentKey, "endpoint"),
                "/pet"
        );
        ComponentParameter type = new ComponentParameter(
                new ComponentParameterKey(componentKey, "type"),
                "PUT"
        );
        ComponentParameter connectionParam = new ComponentParameter(
                new ComponentParameterKey(componentKey, "connection"),
                "Swagger Petstore - OpenAPI 3.0"
        );
        Component component = new Component(
                componentKey,
                "http.request",
                "updatePet",
                "Update an existing pet by Id",
                new ComponentVersion(componentVersionKey, "Update an existing pet by Id"),
                Arrays.asList(endpoint, type, connectionParam),
                new ArrayList<>()
        );

        return new TransformResult(Collections.singletonList(connection), Collections.singletonList(component), title, version);
    }
}
