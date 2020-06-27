<?php
require_once  'db_connect.php';
$con=connect(); 
if (mysqli_connect_errno($con))
{
   echo "400";
}
else {
$uname=$_POST["uname"];
$temp = "SELECT Password FROM UserData where Username='$uname'";
$res=mysqli_query($con,$temp);
if (!$res || mysqli_num_rows($res) == 0)
 echo 'error_username';
if($res)
{
$row = mysqli_fetch_array($res);
$p=$row['Password'];
echo $p;
}
else
echo "401";
}
mysqli_close($con);
?>