package com.example.demo.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailServer {
	@Autowired
	private JavaMailSender mailSender;

	public void sendOrderMail(int activityId, String activityName, String startTime, String endTime, int ticketSize,
			int ticketPrice, int total, String mail) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("funactivityfun@gmail.com");
		helper.setTo(mail);

		helper.setSubject("參加活動通知");
		String content = "<html><body><h3>新參加活動通知</h3>" + " <p>活動名稱:" + activityName + "<p>活動開始時間:" + startTime
				+ "<p>活動結束時間:" + endTime + "<p>票卷價格:" + ticketPrice + "<p>購買張數:" + ticketSize + "<p>總金額:" + total
				+ "<p><a href='http://ec2-54-199-48-212.ap-northeast-1.compute.amazonaws.com/activity/" + activityId + "/detail'>點此查看活動</a>"
				+ "</body></html> ";

		helper.setText(content, true);

		mailSender.send(message);
//		System.out.println("----------mailServer 發射拉-----------");
	}

	public void sendStatusMail(String name, String mail, String userId) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("funactivityfun@gmail.com");
		helper.setTo(mail);

		helper.setSubject("會員認證通知");
		String content = "<html><body><h3> Hi " + name + " <p>歡迎加入Fun活動</h3>"
				+ "<p>點選下方連結完成E-mail認證，成為一般會員後可舉辦免費活動，如需建立付費活動，請至帳號認證完成進階認證" + "<p>"
				+ "<a href='http://ec2-54-199-48-212.ap-northeast-1.compute.amazonaws.com/login'>點此認證會員，並登入</a>" + "</body></html> ";

		helper.setText(content, true);

		mailSender.send(message);
//		System.out.println("----------mailServer 發射拉-----------");
	}

}