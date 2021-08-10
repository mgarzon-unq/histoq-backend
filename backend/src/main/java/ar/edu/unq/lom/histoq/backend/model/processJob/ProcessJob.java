package ar.edu.unq.lom.histoq.backend.model.processJob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.min;

@Data
@Entity(name = "process_job")
public class ProcessJob {
    private @Id
    @GeneratedValue
    Long id;

    private Date begin = new Date();

    private Date end;

    private String goal;

    @Column(length=3000)
    private String errorsDescription;

    private boolean finished = false;

    private boolean withErrors = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private ProcessJob parentJob;

    @OneToMany(mappedBy="parentJob")
    private List<ProcessJob> subProcesses = new ArrayList<ProcessJob>();

    public ProcessJob() {}

    public ProcessJob(String goal) {
        setGoal(goal);
    }

    public ProcessJob(String goal, ProcessJob parentJob) {
        setGoal(goal);
        setParentJob(parentJob);
    }

    public ProcessJob addSubProcess(String goal) {
        ProcessJob subProcess = new ProcessJob(goal, this);
        subProcesses.add(subProcess);
        return subProcess;
    }

    public void finishSuccessfully() {
        finish();
    }

    public void finishWithErrors(String errorsDescription) {
        setWithErrors(withErrors);
        setErrorsDescription(errorsDescription);
        finish();
    }

    public void setErrorsDescription(String errorsDescription) {
        this.errorsDescription =
                errorsDescription!=null && errorsDescription.length()>0 ?
                        errorsDescription.substring(0,min(errorsDescription.length(),3000)-1) :
                        null ;
    }

    private void finish() {
        setEnd(new Date());
        setFinished(true);
    }
}
