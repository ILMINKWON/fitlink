<?php
include("../config/db.php");

$email = $_POST['email'];
$password = password_hash($_POST['password'], PASSWORD_DEFAULT);
$name = $_POST['name'];

$sql = "INSERT INTO users (email, password, name) VALUES (?, ?, ?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("sss", $email, $password, $name);
$stmt->execute();

echo "회원가입 완료";
?>