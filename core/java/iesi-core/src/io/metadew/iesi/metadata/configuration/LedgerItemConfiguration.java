package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.LedgerItem;

public class LedgerItemConfiguration
{

	private LedgerItem ledgerItem;

	private FrameworkExecution frameworkExecution;

	// Constructors
	public LedgerItemConfiguration(LedgerItem ledgerItem, FrameworkExecution frameworkExecution)
	{
		this.setLedgerItem(ledgerItem);
		this.setFrameworkExecution(frameworkExecution);
	}

	public LedgerItemConfiguration(FrameworkExecution frameworkExecution)
	{
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String ledgerName)
	{
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getLedgerMetadataRepository()
					.getTableNameByLabel("LedgerItems");
		sql += " (LEDGER_ID, ITEM_NM, ITEM_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "("
					+ SQLTools.GetLookupIdStatement(
								this.getFrameworkExecution().getMetadataControl().getLedgerMetadataRepository()
											.getTableNameByLabel("Ledgers"),
								"LEDGER_ID", "where LEDGER_NM = '" + ledgerName)
					+ "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getLedgerItem().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getLedgerItem().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public LedgerItem getLedgerItem(long ledgerId, String ledgerItemName)
	{
		LedgerItem ledgerItem = new LedgerItem();
		CachedRowSet crsLedgerItem = null;
		String queryLedgerItem = "select LEDGER_ID, ITEM_NM, ITEM_VAL from "
					+ this.getFrameworkExecution().getMetadataControl().getLedgerMetadataRepository()
								.getTableNameByLabel("LedgerItems")
					+ " where LEDGER_ID = " + ledgerId + " and ITEM_NM = '" + ledgerItemName + "'";
		crsLedgerItem = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
					.executeQuery(queryLedgerItem, "reader");
		try
		{
			while (crsLedgerItem.next())
			{
				ledgerItem.setName(ledgerItemName);
				ledgerItem.setValue(crsLedgerItem.getString("LEDGER_ITEM_VAL"));
			}
			crsLedgerItem.close();
		}
		catch (Exception e)
		{
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return ledgerItem;
	}

	// Getters and Setters
	public LedgerItem getLedgerItem()
	{
		return ledgerItem;
	}

	public void setLedgerItem(LedgerItem ledgerItem)
	{
		this.ledgerItem = ledgerItem;
	}

	public FrameworkExecution getFrameworkExecution()
	{
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution)
	{
		this.frameworkExecution = frameworkExecution;
	}

}