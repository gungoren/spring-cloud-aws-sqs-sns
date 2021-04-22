variable "region" {
  description = "Location for services will be hosted"
  default     = "eu-central1"
}

provider "aws" {
  region = var.region
}