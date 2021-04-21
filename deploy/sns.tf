resource "aws_sns_topic" "alert-payload-topic" {
  name = "alert-payload-topic"
}

resource "aws_sqs_queue" "alert-process" {
  name = "alert-process"
}

resource "aws_sqs_queue" "alert-dump" {
  name = "alert-dump"
}

resource "aws_sns_topic_subscription" "alert-process" {
  topic_arn = aws_sns_topic.alert-payload-topic.arn
  protocol = "sqs"
  endpoint = aws_sqs_queue.alert-process.arn
}

resource "aws_sns_topic_subscription" "alert-dump" {
  topic_arn = aws_sns_topic.alert-payload-topic.arn
  protocol = "sqs"
  endpoint = aws_sqs_queue.alert-dump.arn
}

resource "aws_sqs_queue_policy" "alert-process" {
  queue_url = aws_sqs_queue.alert-process.id
  policy = <<POLICY
{
  "Version":"2012-10-17",
  "Statement":[
    {
      "Sid":"First",
      "Effect":"Allow",
      "Principal":"*",
      "Action":"sqs:SendMessage",
      "Resource":"${aws_sqs_queue.alert-process.arn}",
      "Condition":{
        "ArnEquals":{
          "aws:SourceArn":"${aws_sns_topic.alert-payload-topic.arn}"
        }
      }
    }
  ]
}
POLICY
}


resource "aws_sqs_queue_policy" "alert-dump" {
  queue_url = aws_sqs_queue.alert-dump.id
  policy = <<POLICY
{
  "Version":"2012-10-17",
  "Statement":[
    {
      "Sid":"First",
      "Effect":"Allow",
      "Principal":"*",
      "Action":"sqs:SendMessage",
      "Resource":"${aws_sqs_queue.alert-dump.arn}",
      "Condition":{
        "ArnEquals":{
          "aws:SourceArn":"${aws_sns_topic.alert-payload-topic.arn}"
        }
      }
    }
  ]
}
POLICY
}