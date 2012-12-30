package net.syfaro.LWJGLUpdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class Main {

    private static GUI gui;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui = new GUI();
            }
        });
    }

    public static void load() throws FileNotFoundException {
        gui.getButton().setEnabled(false);
        gui.getStatusLabel().setText("Downloading");
        gui.getProgressBar().setString("Preparing the download");

        updateGui(gui.getButton(), gui.getStatusLabel(), gui.getProgressBar());

        File workDir = OS.getWorkingDirectory();
        workDir.mkdirs();
        File binFolder = new File(workDir, "bin");
        File nativesFolder = new File(binFolder, "natives");
        binFolder.mkdirs();
        nativesFolder.mkdirs();

        List<Download> files = new ArrayList<Download>();
        String[] nativeList = getNatives();
        for (String natives : nativeList) {
            String link = getNativesDownload(natives);
            String name = natives;
            String savePath = new File(nativesFolder, name).getPath();
            Download newDownload = new Download(name, link, savePath);
            files.add(newDownload);
        }

        String[] jarsList = getJars();
        for (String jar : jarsList) {
            String link = getJarsDownload(jar);
            String name = jar;
            String savePath = new File(binFolder, name).getPath();
            Download newDownload = new Download(name, link, savePath);
            files.add(newDownload);
        }

        for (int i = 0; i < files.size(); i++) {
            Download file = files.get((int) i);
            gui.getProgressBar().setString("Downloading " + file.name);
            updateGui(gui.getProgressBar());
            Get.Download(file.url, file.savePath);
            int statusBarValue = (int) (((double) i) / (double) (files.size()) * 100);
            gui.getProgressBar().setValue(statusBarValue);
        }

        gui.getStatusLabel().setText("Done");
        gui.getProgressBar().setValue(100);
        gui.getProgressBar().setString("Done downloading");
        gui.getButton().setText("Exit");
        gui.getButton().setEnabled(true);
        updateGui(gui.getButton(), gui.getStatusLabel(), gui.getProgressBar());
    }

    public static String[] getJars() {
        List<String> jars = new ArrayList<String>();

        jars.add("jinput.jar");
        jars.add("lwjgl.jar");
        jars.add("lwjgl_util.jar");

        return jars.toArray(new String[0]);
    }

    public static String[] getNatives() {
        List<String> natives = new ArrayList<String>();

        switch (OS.getPlatform()) {
            case WINDOWS:
                natives.add("jinput-dx8.dll");
                natives.add("jinput-dx8_64.dll");
                natives.add("jinput-raw.dll");
                natives.add("jinput-raw_64.dll");
                natives.add("lwjgl.dll");
                natives.add("lwjgl64.dll");
                natives.add("OpenAL32.dll");
                natives.add("OpenAL64.dll");
                break;
            case LINUX:
                natives.add("libjinput-linux.so");
                natives.add("libjinput-linux64.so");
                natives.add("liblwjgl.so");
                natives.add("liblwjgl64.so");
                natives.add("libopenal.so");
                natives.add("libopenal64.so");
                break;
            case MACOS:
                natives.add("libjinput-osx.jnilib");
                natives.add("liblwjgl.jnilib");
                natives.add("openal.dylib");
                break;
        }

        return natives.toArray(new String[0]);
    }

    public static String getJarsDownload(String jars) {
        return "http://vps.syfaro.net/lwjgl/jar/" + jars;
    }

    public static String getNativesDownload(String natives) {
        return "http://vps.syfaro.net/lwjgl/native/" + OS.getPlatform().getName() + "/" + natives;
    }

    private static class Download {

        String name;
        String url;
        String savePath;

        public Download(String aName, String aUrl, String save) {
            name = aName;
            url = aUrl;
            savePath = save;
        }
    }

    private static void updateGui(JComponent... objects) {
        for (JComponent component : objects) {
            component.update(component.getGraphics());
        }
    }
}
