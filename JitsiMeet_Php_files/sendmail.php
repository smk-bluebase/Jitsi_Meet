<?php
require 'phpmailer/class.phpmailer.php';
require 'phpmailer/class.smtp.php';
require 'phpmailer/PHPMailerAutoload.php';

function sendMail($email, $subject, $message){
    $mail = new PHPMailer;
    $mail->isSMTP();
    $mail->SMTPDebug = 2;
    $mail->Host = 'smtp.gmail.com';
    $mail->Port = 587;
    $mail->SMTPAuth = true;
    $mail->Pool = true;
    $mail->Username = '';
    $mail->Password = '';

    $mail->setFrom('', 'BlueBase VidCon');
    $mail->addReplyTo('', 'BlueBase VidCon');
    $mail->addAddress($email);

    $mail->isHTML(true);	
    $mail->Subject = $subject;
    $mail->Body = $message;

    if($mail->send()){
        return true;
    }else{
        return false;
    }

}

?>
