resource "aws_dynamodb_table" "api-key" {
  name = "api-key"
  hash_key = "token"
  billing_mode = "PAY_PER_REQUEST"

  attribute {
    name = "token"
    type = "S"
  }
}

resource "aws_dynamodb_table" "rule" {
  name = "rule"
  billing_mode = "PAY_PER_REQUEST"
  hash_key = "customerId"
  range_key = "id"

  attribute {
    name = "customerId"
    type = "S"
  }
  attribute {
    name = "id"
    type = "S"
  }
}