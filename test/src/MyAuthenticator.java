import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {

	String userName=null;
	String password=null;

	public MyAuthenticator(){
	}
	public MyAuthenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}
	protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(userName, password);
	}


public static void main(String[] args){
         //�������Ҫ�������ʼ�
      MailSenderInfo mailInfo = new MailSenderInfo();
      mailInfo.setMailServerHost("smtp.163.com");//smtp.163.com,smtp.qq.com
      mailInfo.setMailServerPort("25");
      mailInfo.setValidate(true);
      mailInfo.setUserName("mjh050505");
      mailInfo.setPassword("[miaojiahang]");//������������
      mailInfo.setFromAddress("mjh050505@163.com");
      mailInfo.setToAddress("334170431@qq.com");
      mailInfo.setSubject("�����ʼ�");
      mailInfo.setContent("�����ʼ�������");
         //�������Ҫ�������ʼ�
      SimpleMailSender sms = new SimpleMailSender();
          sms.sendTextMail(mailInfo);//���������ʽ
//          sms.sendHtmlMail(mailInfo);//����html��ʽ
    }  

}
