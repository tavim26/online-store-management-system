package BusinessLogic;

import Database.CreditCardDAO;
import Model.CreditCard;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreditCardBLL {

    private CreditCardDAO creditCardDAO;


    public CreditCardBLL() {
        creditCardDAO = new CreditCardDAO();
    }


    public void create(CreditCard creditCard) throws SQLException
    {
        creditCardDAO.create(creditCard);
    }

    public void read(CreditCard creditCard) throws SQLException
    {
        creditCardDAO.read(creditCard.getCreditCardID());
    }

    public void update(CreditCard creditCard) throws SQLException
    {
        creditCardDAO.update(creditCard);
    }

    public void delete(CreditCard creditCard) throws SQLException
    {
        creditCardDAO.delete(creditCard.getCreditCardID());
    }

    private int cardNumberValidator(String cardNumber)
    {
        if (cardNumber == null || cardNumber.isEmpty())
        {
            return 1;
        }

        if (!cardNumber.matches("\\d{16}")) {
            return 1;
        }

        return 0;
    }

    private int cardTypeValidator(String cardType)
    {
        if(cardType == null || cardType.isEmpty())
        {
            return 1;
        }

        return 0;
    }

    private int cvvValidator(String cvv)
    {
        if(cvv == null || cvv.isEmpty())
        {
            return 1;
        }

        if(!cvv.matches("\\d{3}"))
        {
            return 1;
        }

        return 0;
    }


    private int balanceValidator(double balance)
    {
        if(balance < 0)
        {
            return 1;
        }
        return 0;
    }


    private int expirationDateValidator(String expirationDate)
    {
        if (expirationDate == null || expirationDate.isEmpty())
        {
            return 1;
        }

        if (!expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return 1;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth cardExpiry = YearMonth.parse(expirationDate, formatter);

            YearMonth currentMonth = YearMonth.now();

            if (cardExpiry.isBefore(currentMonth)) {
                return 1;
            }

            YearMonth maxAllowedExpiry = currentMonth.plusYears(10);
            if (cardExpiry.isAfter(maxAllowedExpiry)) {
                return 1;
            }

        } catch (DateTimeParseException e) {
            return 1;
        }

        return 0;
    }


}
