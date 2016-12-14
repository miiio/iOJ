package com.ioj.wax.ioj;

public class ProblemsInfo {
    private String Title;
    private String Id;
    private String Description;
    private String Input;
    private String Output;
    private String SampleInput;
    private String SampleOutput;

    public ProblemsInfo() {
        SampleInput="";
        SampleOutput="";
        Title="";
        Id="";
        Description="";
        Input="";
        Output="";
    }

    public ProblemsInfo(String title, String id, String description, String input, String output, String sampleInput, String sampleOutput) {
        Title = title;
        Id = id;
        Description = description;
        Input = input;
        Output = output;
        SampleInput = sampleInput;
        SampleOutput = sampleOutput;
    }

    public String getTitle() {
        return Title;
    }

    public String getId() {
        return Id;
    }

    public String getDescription() {
        return Description;
    }

    public String getInput() {
        return Input;
    }

    public String getOutput() {
        return Output;
    }

    public String getSampleInput() {
        return SampleInput;
    }

    public String getSampleOutput() {
        return SampleOutput;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setInput(String input) {
        Input = input;
    }

    public void setOutput(String output) {
        Output = output;
    }

    public void setSampleInput(String sampleInput) {
        SampleInput = sampleInput;
    }

    public void setSampleOutput(String sampleOutput) {
        SampleOutput = sampleOutput;
    }
}

