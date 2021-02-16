package ar.edu.unq.lom.histoq.backend.controller.view;

import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import lombok.Data;
import java.util.Date;

@Data
public class ImageBatchView {
    private Long        id;
    private Date        date;
    private Long        individualId;
    private String      individualLabel;
    private String      individualFullyQualifiedLabel;
    private String      experimentalGroupLabel;
    private String      protocolLabel;
    private String      protocolTitle;
    private Integer     fileCount;
    private boolean     applyStitching;
    private String      userEmail;
    private String      userFirstName;
    private String      userLastName;

    public ImageBatchView(){}

    public ImageBatchView(ImageBatch imageBatch) {
        this.id = imageBatch.getId();
        this.date = imageBatch.getDate();
        this.fileCount = imageBatch.getImageFiles().size();
        this.applyStitching = imageBatch.isApplyStitching();
        this.individualId = imageBatch.getIndividual().getId();
        this.individualLabel = imageBatch.getIndividual().getLabel();
        this.experimentalGroupLabel = imageBatch.getIndividual().getGroup().getLabel();
        this.protocolLabel = imageBatch.getIndividual().getGroup().getProtocol().getLabel();
        this.protocolTitle = imageBatch.getIndividual().getGroup().getProtocol().getTitle();
        this.individualFullyQualifiedLabel = buildIndividualFullyQualifiedLabel(imageBatch.getIndividual());
        this.userEmail = imageBatch.getUser().getEmail();
        this.userFirstName = imageBatch.getUser().getFirstName();
        this.userLastName = imageBatch.getUser().getLastName();
    }

    private String buildIndividualFullyQualifiedLabel(Individual individual) {
        return individual.getGroup().getProtocol().getLabel() + "-" + individual.getGroup().getLabel() + "-" + individual.getLabel();
    }

    public ImageBatch toImageBatch(User user, ProtocolService protocolService) {
        ImageBatch imageBatch = new ImageBatch();
        imageBatch.setId(getId());
        imageBatch.setIndividual(protocolService.findIndividualById(getIndividualId()));
        imageBatch.setApplyStitching(isApplyStitching());
        imageBatch.setUser(user);
        return imageBatch;
    }
}
