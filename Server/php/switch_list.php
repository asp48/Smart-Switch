<?php
require_once  'db_connect.php';
$con=connect(); 
if (mysqli_connect_errno($con))
{
   echo "400";
}
else {
$uname=$_POST["uname"];
$temp = "select Switch1,Switch2,Switch3,Switch4 from CheckPermission where Username = '".$uname."'";
$res=mysqli_query($con,$temp);
if($res)
{
while($row = mysqli_fetch_array($res))
echo $row[0].$row[1].$row[2].$row[3];
}
else
echo "401";
}
mysqli_close($con);
?>