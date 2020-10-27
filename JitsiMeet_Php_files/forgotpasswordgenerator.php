<?php
include("config.php");
require 'sendmail.php';

$db = new DB_Connect();
$con = $db->connect();

$userName = $_POST['username'];

$otp = rand(10000, 100000);

$subject = "BlueBase VC App Forgot Password OTP";

$message = "Dear ".$userName.",<br/><br/>

Looks like you have forgotten your password. Please enter<br/>
the OTP below to authenticate yourself.<br/><br/>

OTP - ".$otp."<br/><br/>

Continue with video conferences online!<br/><br/>

Regards,<br/>
BlueBase Team";

$sql_query = "SELECT count(*), email FROM users WHERE username = '".$userName."'";

$res = mysqli_query($con, $sql_query);

$result = array("status"=>"false");

while($row = mysqli_fetch_array($res)){
    if($row['0'] == 1){
        $sql_query = "INSERT INTO forgotpassword (username, otp) VALUES ('".$userName."', '".$otp."')";

        $con->query($sql_query);

        if(sendMail($row['email'], $subject, $message)){
            $result = array("status"=>"true");
        }
    }
}

echo json_encode([$result]);

mysqli_close($con);
?>