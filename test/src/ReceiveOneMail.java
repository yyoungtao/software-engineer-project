
import java.io.*;          
import java.text.*;          
import java.util.*;          
import javax.mail.*;          
import javax.mail.internet.*;          
     
         
/**       
 * ��һ���ʼ�����Ҫ����һ��ReciveMail����       
 */         
public class ReceiveOneMail {          
    private MimeMessage mimeMessage = null;          
    private String saveAttachPath = ""; //�������غ�Ĵ��Ŀ¼          
    private StringBuffer bodytext = new StringBuffer();//����ʼ�����          
    private String dateformat = "yy-MM-dd HH:mm"; //Ĭ�ϵ���ǰ��ʾ��ʽ          
         
    public ReceiveOneMail(MimeMessage mimeMessage) {          
        this.mimeMessage = mimeMessage;          
    }          
         
    public void setMimeMessage(MimeMessage mimeMessage) {          
        this.mimeMessage = mimeMessage;          
    }          
         
    /**       
     * ��÷����˵ĵ�ַ������       
     */         
    public String getFrom() throws Exception {          
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();          
        String from = address[0].getAddress();          
        if (from == null)          
            from = "";          
        String personal = address[0].getPersonal();    //�����˵�����      
        if (personal == null)          
            personal = "";          
        String fromaddr = personal + "<" + from + ">";    //�����˵ĵ�ַ      
        return fromaddr;          
    }          
         
    /**       
     * ����ʼ����ռ��ˣ����ͣ������͵ĵ�ַ�����������������ݵĲ����Ĳ�ͬ "to"----�ռ��� "cc"---�����˵�ַ "bcc"---�����˵�ַ       
     */         
    public String getMailAddress(String type) throws Exception {          
        String mailaddr = "";          
        String addtype = type.toUpperCase();          
        InternetAddress[] address = null;          
        if (addtype.equals("TO") || addtype.equals("CC")|| addtype.equals("BCC")) {          
            if (addtype.equals("TO")) {          
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);          
            } else if (addtype.equals("CC")) {          
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);          
            } else {          
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);          
            }          
            if (address != null) {          
                for (int i = 0; i < address.length; i++) {          
                    String email = address[i].getAddress();          
                    if (email == null)          
                        email = "";          
                    else {          
                        email = MimeUtility.decodeText(email);          
                    }          
                    String personal = address[i].getPersonal();          
                    if (personal == null)          
                        personal = "";          
                    else {          
                        personal = MimeUtility.decodeText(personal);          
                    }          
                    String compositeto = personal + "<" + email + ">";          
                    mailaddr += "," + compositeto;          
                }          
                mailaddr = mailaddr.substring(0);          
            }          
        } else {          
            throw new Exception("Error emailaddr type!");          
        }          
        return mailaddr;          
    }          
         
    /**    
     * ����������ֵĲ���, ת�� UTF-8 encoding, default Ϊ "" .    
     *     
     * @param request    
     * @param name    
     *            ������.    
     * @return    
     */     
    public static String setDecodeText( String name) {      
        String tmp = "";      
        try {      
            tmp = new String( name.getBytes(      
                    "ISO-8859-1"), "UTF-8");      
        } catch (UnsupportedEncodingException e) {      
        }      
        return tmp;      
    }      
    /**       
     * ����ʼ�����       
     */         
    public String getSubject() throws MessagingException {          
        String subject = "";          
        try {          
            subject = mimeMessage.getSubject();      
            String   header   =   ((MimeMessage)mimeMessage).getHeader("SUBJECT")[0];         
            if   ((header.toLowerCase().indexOf("=?"))>0)         
            {         
                subject   =   new   String((subject.getBytes("iso-8859-1")),"gb2312");         
            }           
     
            if (subject == null)          
                subject = "";          
        } catch (Exception exce) {}          
        return subject;          
    }          
         
    /**       
     * ����ʼ���������       
     */         
    public String getSentDate() throws Exception {          
        Date sentdate = mimeMessage.getSentDate();      
        SimpleDateFormat format = new SimpleDateFormat(dateformat);          
        return format.format(sentdate);          
    }      
    /**    
     * ��ȡ�ռ�ʱ��    
     * @return    
     * @throws Exception    
     */     
    public String getReceivedDate() throws Exception{      
        Date receivedDate = mimeMessage.getReceivedDate();      
        SimpleDateFormat format = new SimpleDateFormat(dateformat);       
        return format.format(receivedDate);      
              
    }      
    /**       
     * ����ʼ���������       
     */         
    public String getBodyText() {          
        return bodytext.toString();          
    }          
         
    /**       
     * �����ʼ����ѵõ����ʼ����ݱ��浽һ��StringBuffer�����У������ʼ� ��Ҫ�Ǹ���MimeType���͵Ĳ�ִͬ�в�ͬ�Ĳ�����һ��һ���Ľ���       
     */         
    public void getMailContent(Part part) throws Exception {          
        String contenttype = part.getContentType();          
        int nameindex = contenttype.indexOf("name");          
        boolean conname = false;          
        if (nameindex != -1)          
            conname = true;          
        System.out.println("CONTENTTYPE: " + contenttype);          
        if (part.isMimeType("text/plain") && !conname) {          
            bodytext.append((String) part.getContent());          
        } else if (part.isMimeType("text/html") && !conname) {          
            bodytext.append((String) part.getContent());          
        } else if (part.isMimeType("multipart/*")) {          
            Multipart multipart = (Multipart) part.getContent();          
            int counts = multipart.getCount();          
            for (int i = 0; i < counts; i++) {          
                getMailContent(multipart.getBodyPart(i));          
            }          
        } else if (part.isMimeType("message/rfc822")) {          
            getMailContent((Part) part.getContent());          
        } else {}          
    }          
         
    /**          
     * �жϴ��ʼ��Ƿ���Ҫ��ִ�������Ҫ��ִ����"true",���򷵻�"false"         
     */          
    public boolean getReplySign() throws MessagingException {          
        boolean replysign = false;          
        String needreply[] = mimeMessage          
                .getHeader("Disposition-Notification-To");          
        if (needreply != null) {          
            replysign = true;          
        }          
        return replysign;          
    }          
         
    /**       
     * ��ô��ʼ���Message-ID       
     */         
    public String getMessageId() throws MessagingException {          
        return mimeMessage.getMessageID();          
    }          
         
    /**       
     * ���жϴ��ʼ��Ƿ��Ѷ������δ�����ط���false,��֮����true��       
     */         
    public boolean isNew() throws MessagingException {          
        boolean isnew = true;          
        Flags flags = ((Message) mimeMessage).getFlags();          
        Flags.Flag[] flag = flags.getSystemFlags();          
        System.out.println("flags's length: " + flag.length);          
        for (int i = 0; i < flag.length; i++) {          
            if (flag[i] == Flags.Flag.SEEN) {          
                isnew = false;          
                System.out.println("seen Message.......");          
                break;          
            }          
        }          
        return isnew;          
    }          
     //���������Ľ�
//    for (int i = 0; i < flag.length; i++) {       
//        if (flag[i] == Flags.Flag.SEEN) { 
//            //��һ����������isnew�ͻ����true; 
//            isnew = true;       
//            System.out.println("seen Message.......");       
//            break;       
//        }       
//    } 

//��ôisNew����false�϶����Ǳ�ʾ���ж����µ��������isNew��ΪisSeen��õ㡣��   

    /**       
     * �жϴ��ʼ��Ƿ��������       
     */         
    public boolean isContainAttach(Part part) throws Exception {          
        boolean attachflag = false;          
        String contentType = part.getContentType();          
        if (part.isMimeType("multipart/*")) {          
            Multipart mp = (Multipart) part.getContent();          
            for (int i = 0; i < mp.getCount(); i++) {          
                BodyPart mpart = mp.getBodyPart(i);          
                String disposition = mpart.getDisposition();          
                if ((disposition != null)          
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition          
                                .equals(Part.INLINE))))          
                    attachflag = true;          
                else if (mpart.isMimeType("multipart/*")) {          
                    attachflag = isContainAttach((Part) mpart);          
                } else {          
                    String contype = mpart.getContentType();          
                    if (contype.toLowerCase().indexOf("application") != -1)          
                        attachflag = true;          
                    if (contype.toLowerCase().indexOf("name") != -1)          
                        attachflag = true;          
                }          
            }          
        } else if (part.isMimeType("message/rfc822")) {          
            attachflag = isContainAttach((Part) part.getContent());          
        }          
        return attachflag;          
    }          
         
    /**          
     * �����渽����          
     */          
    public void saveAttachMent(Part part) throws Exception {          
        String fileName = "";          
        if (part.isMimeType("multipart/*")) {          
            Multipart mp = (Multipart) part.getContent();          
            for (int i = 0; i < mp.getCount(); i++) {          
                BodyPart mpart = mp.getBodyPart(i);          
                String disposition = mpart.getDisposition();          
                if ((disposition != null)          
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition          
                                .equals(Part.INLINE)))) {          
                    fileName = mpart.getFileName();          
                    if (fileName.toLowerCase().indexOf("gbk") != -1) {          
                        fileName = MimeUtility.decodeText(fileName);          
                    }          
                    saveFile(fileName, mpart.getInputStream());          
                } else if (mpart.isMimeType("multipart/*")) {          
                    saveAttachMent(mpart);          
                } else {          
                    fileName = mpart.getFileName();          
                    if ((fileName != null)          
                            && (fileName.toLowerCase().indexOf("GB2312") != -1)) {          
                        fileName = MimeUtility.decodeText(fileName);          
                        saveFile(fileName, mpart.getInputStream());          
                    }          
                }          
            }          
        } else if (part.isMimeType("message/rfc822")) {          
            saveAttachMent((Part) part.getContent());          
        }          
    }          
         
    /**          
     * �����ø������·����          
     */          
         
    public void setAttachPath(String attachpath) {          
        this.saveAttachPath = attachpath;          
    }          
         
    /**       
     * ������������ʾ��ʽ��       
     */         
    public void setDateFormat(String format) throws Exception {          
        this.dateformat = format;          
    }          
         
    /**       
     * ����ø������·����       
     */         
    public String getAttachPath() {          
        return saveAttachPath;          
    }          
         
    /**       
     * �������ı��渽����ָ��Ŀ¼�       
     */         
    private void saveFile(String fileName, InputStream in) throws Exception {          
        String osName = System.getProperty("os.name");          
        String storedir = getAttachPath();          
        String separator = "";          
        if (osName == null)          
            osName = "";          
        if (osName.toLowerCase().indexOf("win") != -1) {          
            separator = "\\";         
            if (storedir == null || storedir.equals(""))         
                storedir = "D:\\tmp";         
        } else {         
            separator = "/";         
            storedir = "/tmp";         
        }      
//        fileName = UIDGenerator.getUID();      
        File storefile = new File(MimeUtility.decodeText(storedir) + MimeUtility.decodeText(separator) + MimeUtility.decodeText(fileName));         
        System.out.println("storefile's path: " + storefile.toString());         
        // for(int i=0;storefile.exists();i++){         
        // storefile = new File(storedir+separator+fileName+i);         
        // }         
        BufferedOutputStream bos = null;         
        BufferedInputStream bis = null;         
        try {         
            bos = new BufferedOutputStream(new FileOutputStream(storefile));         
            bis = new BufferedInputStream(in);         
            int c;         
            while ((c = bis.read()) != -1) {         
                bos.write(c);         
                bos.flush();         
            }         
        } catch (Exception exception) {         
            exception.printStackTrace();         
            throw new Exception("�ļ�����ʧ��!");         
        } finally {         
            bos.close();         
            bis.close();         
        }         
    }         
        
    /**       
     * PraseMimeMessage�����       
     */         
    public static void main(String args[]) throws Exception {         
        Properties props = System.getProperties();         
        props.put("mail.smtp.host", " smtp.qq.com");         
        props.put("mail.smtp.auth", "true");         
        Session session = Session.getDefaultInstance(props, null);   
        //session.setDebug(true);   
        URLName urln = new URLName("pop3", "pop.qq.com", 110, null,         
                "334170431", "mjh13965108553");         
        Store store = session.getStore(urln);         
        store.connect();         
        Folder folder = store.getFolder("INBOX");         
        folder.open(Folder.READ_ONLY);   
        System.out.println(folder.isOpen());   
        Message message[] = folder.getMessages();   
        System.out.println("!1folder.isOpen():"+folder.isOpen());   
        System.out.println("Messages's length: " + message.length);         
        ReceiveOneMail pmm = null;      
        Date sendDate = null;      
        Date today = null;      
        Date day = new Date();      
     
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");      
              
        try{      
        for (int i = 0; i < message.length; i++) {      
            pmm = new ReceiveOneMail((MimeMessage) message[i]);       
            pmm.setDateFormat("yyyy/MM/dd");   
            System.out.println("!2folder.isOpen():"+folder.isOpen());   
            String send_date = pmm.getSentDate();      
            sendDate = format.parse(send_date);      
            today = format.parse(format.format(day));      
            today.compareTo(sendDate);      
            today.after(sendDate);      
            System.out.println("Message " + i + " �����ʼ�: " + pmm.isNew());   //�Ƿ������ʼ�      
            System.out.println("��"+i+"��");      
            System.out.println("======================");         
            System.out.println("Message " + i + " ����: " + pmm.getSubject());   //��ȡ����      
            System.out.println("Message " + i + " ��������: "+ pmm.getSentDate());  //��ȡ�ʼ���������      
            System.out.println("Message " + i + " �Ƿ���Ҫ�ظ�: "+ pmm.getReplySign());//         
            System.out.println("Message " + i + " �Ƿ����ʼ�: " + pmm.isNew());   //�Ƿ������ʼ�      
            System.out.println("Message " + i + "  �Ƿ��������: "+ pmm.isContainAttach((Part) message[i]));   //�Ƿ��������      
            System.out.println("Message " + i + " �����˺͵�: " + pmm.getFrom());   //��ȡ�����˺͵�ַ      
            System.out.println("Message " + i + " �ռ���: "+ pmm.getMailAddress("to"));   //�ռ���      
            System.out.println("Message " + i + " ������: "+ pmm.getMailAddress("cc"));   //������      
            System.out.println("Message " + i + " �ܳ�����: "+ pmm.getMailAddress("bcc")); //�ܳ�����      
//            pmm.setDateFormat("yy��MM��dd�� HH:mm");         
            System.out.println("Message " + i + " �ռ�ʱ�� "+ pmm.getSentDate());         
            System.out.println("Message " + i + " �ʼ���ID: "+ pmm.getMessageId());   //��ȡ�ʼ���ID      
            // ����ʼ�����===============         
            pmm.getMailContent((Part) message[i]);         
            System.out.println("Message " + i + " �ʼ�����: \r\n"         
                    + pmm.getBodyText());         
            System.out.println("������·��"+pmm.getAttachPath());      
            pmm.setAttachPath("C:\\temp\\");          
            pmm.saveAttachMent((Part) message[i]);      
            today.after(sendDate);      
            }   
        folder.close(true);      
        store.close();      
        }catch(Exception e){      
            e.printStackTrace();      
        }      
    }          
}    
