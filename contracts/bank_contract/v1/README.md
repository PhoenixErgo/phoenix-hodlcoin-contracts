# Phoenix HodlCoin: Bank Contract - v1.0.0

## Contract
- [ErgoScript](ergoscript/phoenix_v1_hodlcoin_bank.es)

## Documentation

### Description
Contract for the bank box of the HodlCoin protocol.

### Box Contents
Tokens
1. (BankSingletonId, 1)
2. (HodlCoinTokenId, HodlCoinTokenAmount)

Registers
- R4: Long          TotalTokenSupply
- R5: Long          PrecisionFactor
- R6: Long          MinBankValue
- R7: Long          BankFeeNum 
- R8: Long          DevFeeNum

### Relevant Transactions
1. Mint Tx
- Inputs: Bank, Proxy
- DataInputs: None
- Outputs: Bank, BuyerPK, MinerFee, TxOperatorFee
- Context Variables: None
2. Burn Tx
- Inputs: Bank, Proxy
- DataInputs: None
- Outputs: Bank, BuyerPK, PhoenixFee, MinerFee, TxOperatorFee
- ContextVariables: None

### Compile Time Constants ($)
- $phoenixFeeContractBytesHash: Coll[Byte]

### Context Variables (@)
- None
