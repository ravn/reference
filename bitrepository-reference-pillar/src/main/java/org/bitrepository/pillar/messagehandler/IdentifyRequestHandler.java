package org.bitrepository.pillar.messagehandler;

import org.bitrepository.bitrepositorymessages.MessageRequest;
import org.bitrepository.pillar.common.MessageHandlerContext;
import org.bitrepository.pillar.store.StorageModel;
import org.bitrepository.protocol.MessageContext;
import org.bitrepository.service.exception.RequestHandlerException;

public abstract class IdentifyRequestHandler<T extends MessageRequest> extends PillarMessageHandler<T> {

    /**
     * Constructor.
     * @param context The context for the message handling.
     * @param model The storage model for the pillar.
     */
    protected IdentifyRequestHandler(MessageHandlerContext context, StorageModel model) {
        super(context, model);
    }

    @Override
    public void processRequest(T request, MessageContext requestContext)
            throws RequestHandlerException {
        validateRequest(request, requestContext);
        sendPositiveResponse(request, requestContext);
    }

    /**
     * Validate both that the given request it is possible to perform and that it is allowed.
     * @param request The request to validate.
     * @param requestContext The context for the request.
     * @throws RequestHandlerException If something in the request is inconsistent with the possibilities of the pillar.
     */
    protected abstract void validateRequest(T request, MessageContext requestContext)
            throws RequestHandlerException ;
    
    /**
     * Sends a identification response.
     * @param request The request to respond to.
     * @param requestContext The context for the request.
     * @throws RequestHandlerException If the positive response could not be created.
     */
    protected abstract void sendPositiveResponse(T request, MessageContext requestContext)
            throws RequestHandlerException;
}
