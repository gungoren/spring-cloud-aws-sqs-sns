resource "aws_sqs_queue" "notification" {
  name = "notification"
  visibility_timeout_seconds = 15
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.notification_dlq.arn
    maxReceiveCount = 3
  })
}


resource "aws_sqs_queue" "notification_dlq" {
  name = "notification-dlq"
}

resource "aws_sqs_queue" "alert-payload" {
  name = "alert-payload"
  visibility_timeout_seconds = 15
}
