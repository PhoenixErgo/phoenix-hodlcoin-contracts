{
    // ===== Contract Description ===== //
    // Name: Phoenix HodlCoin Proxy
    // Description: Contract guarding the proxy box for the HodlCoin protocol.
    // Version: 1.0.0
    // Author: mgpai22@github.com

    // ===== Box Contents ===== //

    // Tokens
    // 1. HodlERGTokenId ? Only there if buyer is burning

    // Registers
    // R4: SigmaProp => Buyer SigmaProp
    // R5: Long => Miner Fee
    // R6: Long => Minimum Erg Required to create a box


    // ===== Compile Time Constants ===== //
    // _minerFee: Long // Does not use SELF.R5 because buyer may put too low of a fee
    // _bankSingleton: Coll[Byte]

    // ===== Context Extension Variables ===== //
    // None

    val buyerPK: SigmaProp = SELF.R4[SigmaProp].get
    val validBankBox: Boolean = INPUTS(0).tokens.size > 0 && INPUTS(0).tokens(0)._1 == _bankSingleton

    if (validBankBox) {

        val validMintOrBurnTx: Boolean = {

            // outputs
            val userPKBoxOUT: Box = OUTPUTS(1)

            val validContract: Boolean = (userPKBoxOUT.propositionBytes == buyerPK.propBytes)

            validContract
        }

        // Spend Conditions
        // 1)
        // bank singleton must be in input (one token forever stays in the bank contract, impossible to get out)
        // bank singleton extends all of the conditions in the bank box to the proxy box
        // 2)
        // The buyer address receives the second output (this condition may be redundant/unnecessary )

        sigmaProp(validMintOrBurnTx)

    } else {

        val validRefundTx: Boolean = {

            val validRefundBox: Boolean = {

                //ensures buyer receives total value of box
                val validValueTransfer: Boolean = OUTPUTS.map { (o: Box) =>
                    if (o.propositionBytes == buyerPK.propBytes) o.value else 0L
                }.fold(0L, { (a: Long, b: Long) => a + b }) >= SELF.value

                // if box has tokens it must go to buyer
                val validTokenTransfer: Boolean = {
                    if(SELF.tokens.size > 0){
                        OUTPUTS.exists { (o: Box) =>
                            (o.tokens == SELF.tokens) && (o.propositionBytes == buyerPK.propBytes)
                        }
                    } else{
                      true
                    }
                }


                allOf(Coll(
                    validValueTransfer,
                    validTokenTransfer
                ))
            }

            val validMinerFee: Boolean = OUTPUTS.map { (o: Box) =>
                if (blake2b256(o.propositionBytes) == fromBase16("e540cceffd3b8dd0f401193576cc413467039695969427df94454193dddfb375")) o.value else 0L
            }.fold(0L, { (a: Long, b: Long) => a + b }) == _minerFee

            allOf(Coll(
                validRefundBox,
                validMinerFee
            ))

        }

        sigmaProp(validRefundTx) && buyerPK // buyer must sign tx themself as well

    }

}
