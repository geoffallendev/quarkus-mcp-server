package io.quarkiverse.mcp.server;

import io.quarkiverse.mcp.server.ToolManager.ToolInfo;

/**
 * Any CDI bean that implements this interface is used to determine the set of visible/accesible tools for a specific MCP
 * client.
 * <p>
 * Filters should be fast and efficient, and should never block the current thread (read data from a socket, write data to
 * disk, etc.). If a filter throws an unchecked exception then its execution is ignored and the next filter is applied.
 * <p>
 * Multiple filters are sorted by {@link io.quarkus.arc.InjectableBean#getPriority()} and executed sequentially. Higher priority
 * is executed first. Only features that match all the filters are visible/accesible.
 */
public interface ToolFilter {

    /**
     * Returns {@code true} if the given tool should be considered visible/accesible for a specific MCP client.
     *
     * @param tool (must not be {@code null})
     * @param connection (must not be {@code null})
     * @return {@code true} if visible/accesible, {@code false} otherwise
     */
    boolean test(ToolInfo tool, McpConnection connection);

}
