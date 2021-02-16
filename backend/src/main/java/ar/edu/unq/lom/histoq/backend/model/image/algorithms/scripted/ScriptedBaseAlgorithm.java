package ar.edu.unq.lom.histoq.backend.model.image.algorithms.scripted;

import ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted.ScriptedAlgorithmConfig;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.BaseAlgorithm;
//import org.python.core.*;
//import org.python.util.PythonInterpreter;

public class ScriptedBaseAlgorithm<T> extends BaseAlgorithm<T> {

    //private PythonInterpreter   interpreter;
    //private PyFunction          function;

    protected ScriptedBaseAlgorithm(Class configClass) {
        super(configClass);
        //this.interpreter = new PythonInterpreter();
    }

    protected void loadFunction() {
        ScriptedAlgorithmConfig config = (ScriptedAlgorithmConfig)getConfig();
        /*this.interpreter.exec(config.getFunctionImportStatement());
        this.function = (PyFunction)this.interpreter.get(config.getFunctionName());*/
    }

    /*protected PythonInterpreter getInterpreter(){ return this.interpreter; }

    protected PyFunction getFunction() { return this.function; }

    protected String getStringFromTuple(PyTuple tuple, Integer index) {
        return ((PyString)(tuple.__getitem__(index))).asString();
    }

    protected Integer getIntegerFromTuple(PyTuple tuple, Integer index) {
        return ((PyInteger)(tuple.__getitem__(index))).asInt();
    }

    protected Double getDoubleFromTuple(PyTuple tuple, Integer index) {
        return ((PyFloat)(tuple.__getitem__(index))).asDouble();
    }*/
}
