package io.quarkiverse.mcp.server.test.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.mcp.server.test.Checks;
import io.quarkiverse.mcp.server.test.McpServerTest;
import io.quarkus.test.QuarkusUnitTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ResourcesTest extends McpServerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = defaultConfig()
            .withApplicationRoot(
                    root -> root.addClasses(MyResources.class, Checks.class));

    @Test
    public void testResources() {
        initClient();

        JsonObject resourcesListMessage = newMessage("resources/list");
        send(resourcesListMessage);

        JsonObject resourcesListResponse = waitForLastResponse();

        JsonObject resourcesListResult = assertResponseMessage(resourcesListMessage, resourcesListResponse);
        assertNotNull(resourcesListResult);
        JsonArray resources = resourcesListResult.getJsonArray("resources");
        assertEquals(4, resources.size());

        // alpha, bravo, uni_alpha, uni_bravo
        assertResource(resources.getJsonObject(0), "alpha", null, "file:///project/alpha", null);
        assertResource(resources.getJsonObject(1), "bravo", null, "file:///project/bravo", null);
        assertResource(resources.getJsonObject(2), "uni_alpha", null, "file:///project/uni_alpha", null);
        assertResource(resources.getJsonObject(3), "uni_bravo", null, "file:///project/uni_bravo", null);

        assertResourceRead("1", "file:///project/alpha", "file:///project/alpha");
        assertResourceRead("2", "file:///project/uni_alpha", "file:///project/uni_alpha");
        assertResourceRead("3", "file:///project/bravo", "file:///project/bravo");
        assertResourceRead("4", "file:///foo", "file:///project/uni_bravo");
    }

    private void assertResource(JsonObject resource, String name, String description, String uri, String mimeType) {
        assertEquals(name, resource.getString("name"));
        if (description != null) {
            assertEquals(description, resource.getString("description"));
        }
        assertEquals(uri, resource.getString("uri"));
        if (mimeType != null) {
            assertEquals(description, resource.getString("mimeType"));
        } else {
            assertFalse(resource.containsKey("mimeType"));
        }
    }

    private void assertResourceRead(String expectedText, String expectedUri, String uri) {
        JsonObject resourceReadMessage = newMessage("resources/read")
                .put("params", new JsonObject()
                        .put("uri", uri));
        send(resourceReadMessage);

        JsonObject resourceReadResponse = waitForLastResponse();

        JsonObject resourceReadResult = assertResponseMessage(resourceReadMessage, resourceReadResponse);
        assertNotNull(resourceReadResult);
        JsonArray contents = resourceReadResult.getJsonArray("contents");
        assertEquals(1, contents.size());
        JsonObject textContent = contents.getJsonObject(0);
        assertEquals(expectedText, textContent.getString("text"));
        assertEquals(expectedUri, textContent.getString("uri"));
    }

}
