package fr.altarik;

import okhttp3.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public abstract class CreateTag extends DefaultTask {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Input
    public abstract Property<CreateReleaseData> getConfig();


    @TaskAction
    public void create() throws IOException {
        CreateReleaseData data = getConfig().get();
        String postUrl = data.baseUrl() + "/api/v1/repos/" + data.owner() + "/" + data.repoName() + "/tags";
        RequestBody body = RequestBody.create("""
                {
                    "tag_name": \"""" + data.tagName() + "\"" + """
                }
                """, JSON);
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .header("Authorization", "token " + data.giteaToken())
                .build();
        try(Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful()) {
                if(response.code() != 409)
                    throw new GradleException("Cannot create tag, server answer with code " + response.code() + " and message : " + response.message());
            }
        }
    }

    public record CreateReleaseData(String baseUrl, String owner, String repoName, String tagName, String giteaToken) {
    }


}
