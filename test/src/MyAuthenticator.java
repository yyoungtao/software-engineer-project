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
         //这个类主要是设置邮件
      MailSenderInfo mailInfo = new MailSenderInfo();
      mailInfo.setMailServerHost("smtp.163.com");//smtp.163.com,smtp.qq.com
      mailInfo.setMailServerPort("25");
      mailInfo.setValidate(true);
      mailInfo.setUserName("mjh050505");
      mailInfo.setPassword("[miaojiahang]");//您的邮箱密码
      mailInfo.setFromAddress("mjh050505@163.com");
      mailInfo.setToAddress("334170431@qq.com");
      mailInfo.setSubject("测试邮件");
      mailInfo.setContent("测试邮件的内容");
         //这个类主要来发送邮件
      SimpleMailSender sms = new SimpleMailSender();
          sms.sendTextMail(mailInfo);//发送文体格式
//          sms.sendHtmlMail(mailInfo);//发送html格式
    }  

}
