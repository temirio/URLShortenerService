# URLShortenerService

The project saves to both sql and an in-memory HashMap for faster access.
To avoid SQL Server Setup, comment out all conditional statements and references to the URLDAO class.
It generates a random code {LinkId} and attaches it as a path param to Domain Name "https://bit.ly/{LinkId}".
https://bit.ly/{LinkId} is only used as an example to return results and not a real domain.

Enunwah Stephen
