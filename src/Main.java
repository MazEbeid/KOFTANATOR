import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;


public class Main {

static String deviceId, deviceModel, deviceProcessor;
static Map deviceDetails = new HashMap();
static boolean connectedDevice = false;
static JLabel uninstallSatusLabel,  dNdLabel,apkName;
static  JFrame frame;
static JButton uninstall3rdPartyPackagesButton;


public static void main(String[] args) {

    buildUI();


}

    private static void authenticate (String email, String password, WebDriver driver)
    {

        driver.get("https://play.google.com/store");
        driver.findElement(By.xpath("//*[@id=\"gb_70\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"identifierId\"]")).sendKeys(email);
        driver.findElement(By.xpath("//*[@id=\"identifierNext\"]/content/span")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"passwordNext\"]/content/span")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String downloadApk(String packageName, WebDriver driver)
    {
        WebDriverWait wait = new WebDriverWait(driver, 2);
        String result = "";
        try{
            driver.get("https://play.google.com/store/apps/details?id="+packageName);
            Thread.sleep(5000);
            if(driver.findElement(By.xpath("//*[@id=\"fcxH9b\"]/div[5]/c-wiz/div/div[2]/div/div[1]/div/c-wiz[1]/c-wiz[1]/div/div[2]/div/div[2]/div/div[2]/div[2]/c-wiz/c-wiz/div/span/button")).isDisplayed())
            {
                driver.findElement(By.cssSelector("#fcxH9b > div.WpDbMd > c-wiz > div > div.ZfcPIb > div > div.JNury.Ekdcne > div > c-wiz:nth-child(1) > c-wiz:nth-child(1) > div > div.D0ZKYe > div > div.wE7q7b > div > div.hfWwZc > div.NznqUc > c-wiz > c-wiz > div > span > button")).click();
                Thread.sleep(4000);
                driver.switchTo().frame(driver.switchTo().activeElement()).findElement(By.xpath("//*[@id=\"purchase-ok-button\"]")).click(); //confirm install button
                System.out.print(packageName+" installed");
                result =  (packageName+" installed");
                Thread.sleep(4000);
            }
            else
            {
                result = packageName+" failed to install";
            }



        }
        catch(Exception e)
        {
            result = packageName+" failed to install";
            e.printStackTrace();
        }
        return result;


    }
    private static String downloadable(String packageName) {
       String result = "";
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
            Thread.sleep(3000);
          //  System.out.println(doc.toString());
            if (doc.toString().contains("Buy</button")) {
                System.out.println(packageName + " requires payment");
                result =  packageName+" requires payment";
             //   flag =  false;
            } //if (doc.toString().contains("installed")) {
                //do nothing
             //   System.out.println(packageName + " is already installed");
             //   flag =  false;
           // }


        } catch (Exception e) {
            System.out.println(packageName + " Couldn't find store listing");
            result =  packageName+" couldn't find store listing";

        }
        System.out.println(result);
       return result;

    }

    private static void buildUI() {

        frame = new JFrame("The Batch-en-nator");
        frame.setPreferredSize(new Dimension(400,500));
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



/////////////////////////////////////////////////////////// Batchentor Panel////////////////////////////////////////////
        JPanel batchenatorPanel = new JPanel();
        batchenatorPanel.setLayout(null);

        JLabel label = new JLabel();
        label.setText("Package names");
        label.setBounds(10, 10, 100, 20);
        Font labelFont = new Font(Font.SANS_SERIF,  Font.BOLD, 12);
        label.setFont(labelFont);

        JTextArea packagesArea= new JTextArea();

        packagesArea.setAutoscrolls(true);
        JScrollPane  scrollPane = new JScrollPane(packagesArea);
        scrollPane.setBounds(10, 30, 380, 200);

        JLabel statusLabel = new JLabel();
        statusLabel.setText("Status:");
        statusLabel.setBounds(10, 250, 500, 20);
        statusLabel.setFont(labelFont);

        JTextArea statusText = new JTextArea();
        statusText.setEditable(false);

        JScrollPane  statusScroll = new JScrollPane(statusText);
        statusScroll.setBounds(10, 270, 380, 100);



        JButton downloadButton=new JButton("Download");
        downloadButton.setBounds(130,390,140, 40);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String packageNames = packagesArea.getText();
                statusText.setText("");

                if(packageNames.isEmpty())
                {
                    statusLabel.setText("Status: please insert at least one package name");
                }
                else
                {
                    String []packagesToInstall = packageNames.split("\n");
                    statusLabel.setText("Status: attempted to download "+packagesToInstall.length+" apks");

                    ChromeOptions chromeOptions = new ChromeOptions();
                   // chromeOptions.addArguments("--headless");
                   WebDriver  driver = new ChromeDriver(chromeOptions);

                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(10,10));
                    driver.manage().window().setPosition(new org.openqa.selenium.Point(0,510));


                    authenticate("appdroidsmena@gmail.com","testmena", driver);
                    for (String packageName : packagesToInstall) {
                        if(downloadable(packageName).isEmpty())
                        {
                           statusText.append(downloadApk(packageName, driver));
                           statusText.append("\n");
                           frame.repaint();
                           frame.revalidate();
                        }
                        else
                        {
                            statusText.append(downloadable(packageName));
                            statusText.append("\n");
                            frame.repaint();
                            frame.revalidate();
                        }

                    }
                   driver.close();

                }

            }
        });
        ImageIcon loading = new ImageIcon("ajax-loader.gif");
        JLabel loadingLabel = new JLabel("", loading, JLabel.CENTER);
        loadingLabel.setBounds(150,7,100,30);
        batchenatorPanel.add(loadingLabel);


        batchenatorPanel.add(label);
        batchenatorPanel.add(scrollPane);
        batchenatorPanel.add(statusLabel);
        batchenatorPanel.add(statusScroll);
        batchenatorPanel.add(downloadButton);
        batchenatorPanel.setSize(500,500);
        batchenatorPanel.setVisible(true);


/////////////////////////////////////////////////////////// Batchentor Panel ///////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////// Clean phone panel ///////////////////////////////////////////

        JPanel cleanDeviceMainPanel = new JPanel();
        cleanDeviceMainPanel.setLayout(null);

        JLabel connectedDeviceWarning = new JLabel();
        connectedDeviceWarning.setVisible(false);
        connectedDeviceWarning.setText("No devices connected");
        connectedDeviceWarning.setBounds(150, 200, 200, 20);
        Font statusFont = new Font(Font.SANS_SERIF,  Font.BOLD, 12);
        connectedDeviceWarning.setFont(statusFont);
        cleanDeviceMainPanel.add(connectedDeviceWarning);


        JPanel connectedDevicePanel = new JPanel();
        connectedDevicePanel.setLayout(null);
        connectedDevicePanel.setSize(500,500);


        JLabel deviceModelLabel = new JLabel();
        deviceModelLabel.setText("Device Model: please wait . . . ");
        deviceModelLabel.setBounds(10, 10, 300, 20);
        Font deviceModelLabelFont = new Font(Font.SANS_SERIF,  Font.BOLD, 12);
        deviceModelLabel.setFont(deviceModelLabelFont);
        connectedDevicePanel.add(deviceModelLabel);


        JLabel deviceProcessorLabel = new JLabel();
        deviceProcessorLabel.setText("Device Processor: please wait . . .");
        deviceProcessorLabel.setBounds(10, 30, 300, 20);
        Font deviceProcessorFont = new Font(Font.SANS_SERIF,  Font.BOLD, 12);
        deviceProcessorLabel.setFont(deviceProcessorFont);
        connectedDevicePanel.add(deviceProcessorLabel);


        JLabel deletPackagesLabel = new JLabel();
        deletPackagesLabel.setText("Package names to uninstall");
        deletPackagesLabel.setBounds(10, 70, 300, 20);
        Font deletPackagesLabelFont = new Font(Font.SANS_SERIF,  Font.BOLD, 12);
        deletPackagesLabel.setFont(deletPackagesLabelFont);
        connectedDevicePanel.add(deletPackagesLabel);


        JTextArea deletePackagesArea = new JTextArea();

        deletePackagesArea.setAutoscrolls(true);
        JScrollPane  deletePackagesAreaScroll = new JScrollPane(deletePackagesArea);
        deletePackagesAreaScroll.setBounds(10, 100, 380, 150);
        connectedDevicePanel.add(deletePackagesAreaScroll);


        JButton uninstallButton =new JButton("Uninstall Packages");
        uninstallButton.setBounds(100,250,140, 40);
        connectedDevicePanel.add(uninstallButton);
        uninstallButton.addActionListener(e -> {
            String packageNames = deletePackagesArea.getText();
            String [] deletePackages = packageNames.split("\n");

            uninstallPackages(deletePackages);

        });



        uninstall3rdPartyPackagesButton =new JButton("Uninstall All 3rd Party Packages");
        uninstall3rdPartyPackagesButton.setBounds(50,290,250, 40);
        uninstall3rdPartyPackagesButton.setBackground(Color.CYAN);
        connectedDevicePanel.add(uninstall3rdPartyPackagesButton);
        uninstall3rdPartyPackagesButton.addActionListener(e -> uninstall3rdPartyPackages());


        uninstallSatusLabel= new JLabel("Status: ");
        uninstallSatusLabel.setBounds(10,330,490,40);
        connectedDevicePanel.add(uninstallSatusLabel);

        cleanDeviceMainPanel.add(connectedDevicePanel);
        cleanDeviceMainPanel.setSize(500,500);
        cleanDeviceMainPanel.setVisible(false);



///////////////////////////////////////////////////////// Clean phone panel ////////////////////////////////////////////

//////////////////////////////////////////////////////// Sideloading panel /////////////////////////////////////////////



        JPanel  sideloadPanel = new JPanel();
        sideloadPanel.setLayout(null);
        sideloadPanel.setVisible(false);
        sideloadPanel.setSize(500,500);
        sideloadPanel.setVisible(false);

        dNdLabel = new JLabel();
        dNdLabel.setVisible(false);
        dNdLabel.setText("Drag and drop files here");
        dNdLabel.setBounds(100, 200, 400, 20);
        dNdLabel.setFont(statusFont);
        sideloadPanel.add(dNdLabel);

        apkName = new JLabel();
        apkName.setVisible(true);
        apkName.setText("");

        apkName.setBounds(10, 250, 400, 20);

        apkName.setFont(statusFont);
        sideloadPanel.add(apkName);


        MyDragDropListener dragDropListener =new MyDragDropListener();

        new DropTarget(sideloadPanel, dragDropListener);

        JLabel selectLabel = new JLabel();
        selectLabel.setVisible(true);
        selectLabel.setText("Select Device");
        selectLabel.setBounds(10, 10, 400, 20);
        selectLabel.setFont(statusFont);
        sideloadPanel.add(selectLabel);


       JRadioButton wearRadio =	new JRadioButton("Wear");
       wearRadio.setBounds(10, 40, 100, 20);

       sideloadPanel.add(wearRadio);

        JLabel textFieldLabel = new JLabel();
        textFieldLabel.setVisible(true);
        textFieldLabel.setText("Enter Wear IP Address");
        textFieldLabel.setBounds(25, 70, 400, 20);
        textFieldLabel.setFont(statusFont);
        textFieldLabel.setVisible(false);
        sideloadPanel.add(textFieldLabel);

       JTextField wearIPField = new JTextField();
       wearIPField.setBounds(20,90,200,35);
       wearIPField.setVisible(false);
       wearIPField.setBackground(Color.white);
       sideloadPanel.add(wearIPField);

       JButton connectToWear = new JButton("Connect");
       connectToWear.setVisible(false);
       connectToWear.setBounds(100,150,100,40);
       sideloadPanel.add(connectToWear);

        JRadioButton phoneRadio =	new JRadioButton("Phone");
        phoneRadio.setBounds(150, 40, 200, 20);
        phoneRadio.setSelected(false);
        sideloadPanel.add(phoneRadio);



       wearRadio.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               connectToWear.setVisible(true);
               textFieldLabel.setVisible(true);
               wearIPField.setVisible(true);
               phoneRadio.setSelected(false);
               wearRadio.setEnabled(false);
               phoneRadio.setEnabled(true);
               dNdLabel.setVisible(false);

           }
       });

       phoneRadio.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               wearRadio.setSelected(false);
               connectToWear.setVisible(false);
               textFieldLabel.setVisible(false);
               wearIPField.setVisible(false);
               wearRadio.setEnabled(true);
               phoneRadio.setEnabled(false);
               dNdLabel.setVisible(true);

           }
       });
/////////////////////////////////////////////////////// Sideloading panel //////////////////////////////////////////////
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenuItem  cleanDeviceMenuItem = new JMenuItem();
        cleanDeviceMenuItem.addActionListener(e -> {
            batchenatorPanel.setVisible(false);
            cleanDeviceMainPanel.setVisible(true);
            sideloadPanel.setVisible(false);
            frame.setTitle("Clean device");
            frame.repaint();

            try
            {
                Runtime rt = Runtime.getRuntime();
                Process getDeviceId = rt.exec("adb devices");
                new Thread(new Runnable() {
                    public void run() {
                        BufferedReader input = new BufferedReader(new InputStreamReader(getDeviceId.getInputStream()));

                        try {
                            Stream <String> lines;
                            lines =  input.lines();
                            deviceId = lines.toArray()[1].toString().split("device")[0].trim();
                            System.out.println("Device ID: "+ deviceId);

                            Process getDeviceModel = rt.exec("adb -s "+deviceId+" shell getprop ro.product.model");
                            input = new BufferedReader((new InputStreamReader(getDeviceModel.getInputStream())));

                            lines = input.lines();

                            deviceModel = lines.toArray()[0].toString();
                            System.out.println("Device Model: "+ deviceModel);

                            Process getProcessor = rt.exec("adb -s "+deviceId+" shell getprop ro.product.cpu.abi");
                            input = new BufferedReader((new InputStreamReader(getProcessor.getInputStream())));

                            lines = input.lines();
                            deviceProcessor = lines.toArray()[0].toString();
                            System.out.println("Device Processor: "+ deviceProcessor);

                            connectedDevice = true;
                            connectedDevicePanel.setVisible(true);
                            deviceModelLabel.setText("Device Model: "+ deviceModel);
                            deviceProcessorLabel.setText("Device Processor: "+ deviceProcessor);
                            connectedDeviceWarning.setVisible(false);
                            frame.repaint();

                        } catch (Exception e) {
                            connectedDevice = false;
                            System.out.println("No connected devices");
                            connectedDevicePanel.setVisible(false);
                            connectedDeviceWarning.setVisible(true);
                            frame.repaint();

                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            catch (Exception exc)
            {

                exc.printStackTrace();
            }


        });

        JMenuItem  batchDownloadApksMenuItem  = new JMenuItem();
        batchDownloadApksMenuItem.addActionListener(e -> {

            cleanDeviceMainPanel.setVisible(false);
            batchenatorPanel.setVisible(true);
            sideloadPanel.setVisible(false);
            frame.setTitle("The Bacth-en-nator");
            frame.repaint();
            frame.repaint();


        });

        JMenuItem  sideLoadApkMenuItem = new JMenuItem();
        sideLoadApkMenuItem.addActionListener(e -> {

            cleanDeviceMainPanel.setVisible(false);
            batchenatorPanel.setVisible(false);
            sideloadPanel.setVisible(true);
            frame.setTitle("Side loading");
            frame.repaint();

        });

        menuBar.add(menu);
        batchDownloadApksMenuItem.setText("The Batch-en-ator");
        cleanDeviceMenuItem.setText("Clean device");
        sideLoadApkMenuItem.setText("Side loading");

        menu.add(batchDownloadApksMenuItem);
        menu.add(sideLoadApkMenuItem);
        menu.add(cleanDeviceMenuItem);

        frame.setJMenuBar(menuBar);
        frame.add(batchenatorPanel);
        frame.add(cleanDeviceMainPanel);
        frame.add(sideloadPanel);
        frame.setSize(500,500);
        frame.setJMenuBar(menuBar);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

 public  static  void uninstall3rdPartyPackages() {
uninstall3rdPartyPackagesButton.setEnabled(false);
frame.repaint();
        try
        {

            Runtime rt = Runtime.getRuntime();
            Process get3rdPartyPackages = rt.exec("adb shell pm list packages -3");
            new Thread(new Runnable() {
                int count = 0;
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(get3rdPartyPackages.getInputStream()));
                    Queue<String> packageNamesToUninstall =  new LinkedList<String>();
                    try {
                        Stream <String> lines;
                        lines =  input.lines();

                        for (Object o : lines.toArray()) {

                            ((LinkedList<String>) packageNamesToUninstall).push(o.toString().split("package:")[1]);
                        }
                        if(packageNamesToUninstall.size()==0)
                        {
                            uninstallSatusLabel.setText("Status: Couldn't find any 3rd part apps to uninstall");
                            frame.repaint();
                        }
                        else
                        {
                            while(packageNamesToUninstall.peek()!=null)
                            {
                                String temp = packageNamesToUninstall.peek();
                                if(!temp.contains("google"))
                                {
                                    System.out.println("Uninstalling "+(temp));
                                    Process uninstallPackage = rt.exec("adb uninstall "+((LinkedList<String>) packageNamesToUninstall).pop());
                                    input = new BufferedReader(new InputStreamReader(uninstallPackage.getInputStream()));
                                    lines = input.lines();
                                    for (Object o : lines.toArray()) {
                                        System.out.println("Status :"+o.toString());
                                        uninstallSatusLabel.setText("Status: Uninstalling "+temp+" - "+o.toString());
                                        frame.repaint();
                                    }
                                    count ++;
                                }
                                else
                                {
                                    System.out.println("Skipping Google app: "+((LinkedList<String>) packageNamesToUninstall).pop());
                                }


                            }
                            uninstallSatusLabel.setText("Status: uninstalled "+count+" apps");
                            frame.repaint();
                        }



                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception exc)
        {

            exc.printStackTrace();
        }
     uninstall3rdPartyPackagesButton.setEnabled(true);
     frame.repaint();
}

    public static void uninstallPackages(String [] packages) {



        for(String packageName: packages)
        {
            System.out.println(packageName);

            // <app package name>
            try
            {
                Process uninstallPackagesProces;
                Runtime rt = Runtime.getRuntime();
                uninstallPackagesProces = rt.exec("adb uninstall "+packageName);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uninstallPackagesProces.getInputStream()));

                Stream <String> lines;
                lines =  bufferedReader.lines();
                for (Object o : lines.toArray()) {
                    System.out.println("Status :"+o.toString());
                    uninstallSatusLabel.setText("Status: Uninstalling "+packageName+" - "+o.toString());
                    frame.repaint();
                }

                Thread.sleep(2000);
               // System.out.println("Deleted: "+packageName);
            }

            catch (Exception exc)
            {
                exc.printStackTrace();
                System.out.println("Failed to delete: "+packageName);
            }

        }

    }

}

