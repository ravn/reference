package org.bitrepository.service.contributor.handler;

import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.bitrepositoryelements.ResponseInfo;
import org.bitrepository.bitrepositoryelements.ResultingStatus;
import org.bitrepository.bitrepositoryelements.StatusCode;
import org.bitrepository.bitrepositoryelements.StatusInfo;
import org.bitrepository.bitrepositorymessages.GetStatusFinalResponse;
import org.bitrepository.bitrepositorymessages.GetStatusRequest;
import org.bitrepository.bitrepositorymessages.MessageRequest;
import org.bitrepository.bitrepositorymessages.MessageResponse;
import org.bitrepository.common.utils.CalendarUtils;
import org.bitrepository.service.contributor.ContributorContext;

public class GetStatusRequestHandler extends AbstractRequestHandler {
    private final ContributorContext context;

    public GetStatusRequestHandler(ContributorContext context) {
        this.context = context;
    }

    @Override
    public Class getRequestClass() {
        return GetStatusRequest.class;
    }

    @Override
    public void processRequest(MessageRequest request) {
        GetStatusFinalResponse response = new GetStatusFinalResponse();
        ResultingStatus status = new ResultingStatus();
        status.setStatusTimestamp(CalendarUtils.getNow());
        status.setStatusInfo(getStatus());
        response.setResultingStatus(status);

        ResponseInfo info = new ResponseInfo();
        info.setResponseCode(ResponseCode.OPERATION_COMPLETED);
        info.setResponseText("Returning status");
        response.setResponseInfo(info);

        response.setContributor(context.getComponentID());

        dispatchResponse(request, response);

    }

    @Override
    public MessageResponse generateFailedResponse() {
        return new GetStatusFinalResponse();
    }

    protected StatusInfo getStatus() {
        StatusInfo status = new StatusInfo();
        status.setStatusCode(StatusCode.OK);
        status.setStatusText("Ok");
        return status;
    }

    @Override
    protected ContributorContext getContext() {
        return context;
    }
}
