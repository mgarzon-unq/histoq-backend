package ar.edu.unq.lom.histoq.backend.model.image;

import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import ar.edu.unq.lom.histoq.backend.model.user.User;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Data
@Entity (name = "image_batch")
public class ImageBatch {
    private @Id
    @GeneratedValue
    Long id;
    private Date date = new Date();
    private boolean applyStitching = false;
    @ManyToOne
    private Individual individual;
    @ManyToOne
    private User user;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="batch_id")
    private List<ImageFile> imageFiles = new ArrayList<ImageFile>();

    public ImageBatch(){}

    public void addImageFile(ImageFile imageFile) {
        getImageFiles().add(imageFile);
    }

    public List<Image> process(ImageScanner scanner) {

        return ( isApplyStitching() ? stitchImageFiles(scanner): getImageFiles() )  // select the files to process, depending on the stitching mode...
                .stream()
                .map( imageFile -> processImageFile(imageFile,scanner))
                .collect(Collectors.toList());
    }

    private Image processImageFile(ImageFile imageFile, ImageScanner scanner) {

        Image image = scanner.processImageFile(imageFile);

        image.addImageFile(imageFile);  // reference the results so then
        imageFile.setImage(image);      // persistence save the changes..

        return image;
    }

    private List<ImageFile> stitchImageFiles(ImageScanner scanner) {

        // remove existing files created by stitching...
        getImageFiles().removeAll(getImageFiles().stream().filter(ImageFile::isStitched).collect(Collectors.toList()));

        // group regular files by their stitching group...
        Map<String, List<ImageFile>> groupedImageFiles = getImageFiles()
                .stream()
                .collect(groupingBy(ImageFile::getStitchingGroup));

        // send each group to the stitching process...
        return groupedImageFiles.entrySet().stream().map( group -> {
                    ImageFile imageFile = scanner.stitchImageFiles(group.getValue(),group.getKey());
                    imageFile.setStitched(true);    // tag the new file as created by stitching...
                    imageFile.setBatch(this);       // reference the results so then
                    getImageFiles().add(imageFile); //    persistence save the changes...
                    return imageFile;
                })
                .collect(Collectors.toList());
    }

}
