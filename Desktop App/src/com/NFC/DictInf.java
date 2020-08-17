package com.NFC;

public class DictInf {
    //For debugging purposes, it makes sense to display messages as strings that become bytes
    //Identification mode
    //Messages sent by Reader
    public static final byte[] Rwaitid="waitPlease".getBytes();//"waitPlease".getBytes();
    public static final byte[] RAskForIdentModeid="GetModeIdentify".getBytes();

    public static final byte[] RAskForU2id="GetU2".getBytes();

    public static final byte[] RAskForP2id="GetP2".getBytes();
    public static final byte[] RCommitP2id="CommitU2".getBytes();
    public static final byte[] RokGoodid="rr0kggOOd".getBytes();
    public static final byte[] RokNotGoodid="notG00d".getBytes();

    //Messages sent from cell phone
    public static final byte[] HcommitWaitid="okWait".getBytes();
    public static final byte[] HAIDSelectionOkid="AIDSelectionOk".getBytes();
    public static final byte[] HIdentModeSelectedid="IdentModeInitialized".getBytes();

    public static final byte[] HAskForModeid="AskForMode".getBytes();
    public static final byte[] HUPrefixid="UR: ".getBytes();
    public static final byte[] HDoneWithIdid="DoneWithIdProcess".getBytes();
}
