<?php
	if(!isset($db)){
		require_once('connect.php');
	}
	session_start;
	
	$reg_first_name = $_POST['reg_first_name'];
	$reg_last_name = $_POST['reg_last_name'];
	$reg_username = $_POST['reg_username'];
	$reg_email = $_POST['reg_email'];
    $reg_password = $_POST['reg_password'];
    $reg_confirm_password = $_POST['reg_confirm_password'];
    $reg_account_type = $_POST['reg_account_type'];
    
    if($reg_password == $reg_confirm_password) {
	    $query = "INSERT INTO users(first_name, last_name, username, email_address, password, account_type)
                VALUES('$reg_first_name', '$reg_last_name', '$reg_username', '$reg_email', '$reg_password', '$reg_account_type')";	
        $query2 = "SELECT first_name, last_name, username, email_address FROM users";
        
		$response = @mysqli_query($db, $query2);
		
		if($response) {
			if($row == null) {
				echo 'Registration successful! Welcome, ' . $reg_first_name . '!';
		        echo '<br><br> <a href="profile.php">Proceed</a>';
		        mysqli_query($db, $query);
		        $_SESSION['username'] = $reg_username;
		        $_SESSION['logged_in'] = true;
		        exit();
			}
		    while($row = mysqli_fetch_array($response)) {
		        if(($reg_first_name == $row['first_name'] && $reg_last_name == $row['last_name'])) {
		           	echo 'A user with that name already exists.';
		           	echo '<br><br> <a href="register.php">Back</a>';
		           	exit();
		        } else if ($row['username'] == $reg_username) {
		        	echo 'A user with that username already exists.';
		        	echo '<br><br> <a href="register.php">Back</a>';
		        	exit();
		        } else if ($reg_email == $row['email_address']) {
		        	echo 'A user with that email already exists.';
		        	echo '<br><br> <a href="register.php">Back</a>';
		        	exit();
		        } else {
		        	echo 'Registration successful! Welcome, ' . $reg_first_name . '!';
		            echo '<br><br> <a href="profile.php">Proceed</a>';
		            mysqli_query($db, $query);
		            $_SESSION['username'] = $reg_username;
		            $_SESSION['logged_in'] = true;
		            exit();
		        }
		    }
		}
	} else {
		echo 'The two passwords do not match. Please re-enter.';
		echo '<br><br> <a href="register.php">Back</a>';
		exit();
	}
	
	$response = @mysqli_query($db, $query);
?>