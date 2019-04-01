package com.github.dynamicextensionsalfresco.webscripts.resolutions;

/**
 * @author Laurent Van der Linden
 */
public class StatusResolution extends AbstractResolution {
    private int status;
    private String message;

    public StatusResolution(int status) {
        this.status = status;
    }

    public StatusResolution(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public void resolve() throws Exception {
        getResponse().setStatus(status);
        if (message != null) {
            getResponse().getWriter().append(message);
        }
    }
}
