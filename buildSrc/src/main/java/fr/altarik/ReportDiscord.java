package fr.altarik;

import okhttp3.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public abstract class ReportDiscord extends DefaultTask {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Input
    public abstract Property<ReportData> getConfig();

    private final OkHttpClient client = new OkHttpClient();

    @TaskAction
    public void report() throws IOException {
        ReportData data = getConfig().get();
        String message = data.message();
        RequestBody body = RequestBody.create("""
                {
                    "embeds": [
                        {
                            "title": "A new update of\s""" + data.projectName() + """
                             is available",
                            "description":\s""" + "\"" + message + "\"" + """
                            ,
                            "url":\s""" + "\"" + data.url() + "\"" + """
                        }
                    ]
                }
                """, JSON);

        String url = data.baseUrl() + "/webhooks/" + data.webhookId + "/" + data.webhookToken;
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try(Response response = client.newCall(request).execute()) {
            getLogger().info("report sent");
            if(!response.isSuccessful()) {
                throw new GradleException("Discord returned a " + response.code() + " code: " + response.message());
            }
        }
    }

    public record ReportData(String baseUrl, String webhookId, String webhookToken, String projectName, String message, String url) {

    }

}
