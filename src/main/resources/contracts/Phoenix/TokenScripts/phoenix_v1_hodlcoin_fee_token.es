{

    // ===== Contract Information ===== //
    // Name: Phoenix HodlCoin Fee
    // Description: Contract guarding the fee box for the HodlCoin protocol.
    // Version: 1.0.0
    // Author: Luca D'Angelo (ldgaetano@protonmail.com), MGPai

    // ===== Box Contents ===== //
    // Tokens
    // None
    // Registers
    // None

    // ===== Relevant Transactions ===== //
    // 1. Fee Distribution Tx
    // Inputs: PhoenixFee1, ... , PhoenixFeeM
    // DataInputs: None
    // Outputs: Dev1PK, Dev2PK, Dev3PK, PhoenixPK
    // Context Variables: None

    // ===== Compile Time Constants ($) ===== //
    // $tokenId

    // ===== Context Variables (@) ===== //
    // None

    val devPercentage: (Long, Long) = (60L, 100L)
    val phoenixPercentage: (Long, Long) = (40L, 100L)

    // ===== Relevant Variables ===== //
    val dev1Address: SigmaProp                  = PK("9hHondX3uZMY2wQsXuCGjbgZUqunQyZCNNuwGu6rL7AJC8dhRGa")
    val dev2Address: SigmaProp                  = PK("9gnBtmSRBMaNTkLQUABoAqmU2wzn27hgqVvezAC9SU1VqFKZCp8")
    val dev3Address: SigmaProp                  = PK("9iE2MadGSrn1ivHmRZJWRxzHffuAk6bPmEv6uJmPHuadBY8td5u")
    val phoenixAddress: SigmaProp               = PK("9iPs1ujGj2eKXVg82aGyAtUtQZQWxFaki48KFixoaNmUAoTY6wV")

    // ===== Fee Distribution Tx ===== //
    val validFeeDistributionTx: Boolean = {

        // Outputs
        val dev1BoxOUT: Box     = OUTPUTS(0)
        val dev2BoxOUT: Box     = OUTPUTS(1)
        val dev3BoxOUT: Box     = OUTPUTS(2)
        val phoenixBoxOUT: Box  = OUTPUTS(3)

        val devAmount: Long = OUTPUTS.map({ (output: Box) => output.tokens(0)._2 }).fold(0L, { (acc: Long, curr: Long) => acc + curr })

        val validPercentages: Boolean = {

            (devPercentage._1 * phoenixPercentage._2 + phoenixPercentage._1 * devPercentage._2) == (devPercentage._2 * phoenixPercentage._2) // (a/b + c/d = 1 => ad + cb = bd)

        }

        val validDevBoxes: Boolean = {

            val devAllocation: Long = ((devPercentage._1 * devAmount) / devPercentage._2) / 3L

            allOf(Coll(
                (dev1BoxOUT.tokens(0)._1 == $tokenId),
                (dev1BoxOUT.tokens(0)._2  == devAllocation),
                (dev1BoxOUT.propositionBytes == dev1Address.propBytes),
                (dev3BoxOUT.tokens(0)._1 == $tokenId),
                (dev2BoxOUT.tokens(0)._2  == devAllocation),
                (dev2BoxOUT.propositionBytes == dev2Address.propBytes),
                (dev3BoxOUT.tokens(0)._1 == $tokenId),
                (dev3BoxOUT.tokens(0)._2  == devAllocation),
                (dev3BoxOUT.propositionBytes == dev3Address.propBytes)
            ))

        }

        val validPhoenixBox: Boolean = {
            allOf(Coll(
                (phoenixBoxOUT.tokens(0)._1 == $tokenId),
                (phoenixBoxOUT.tokens(0)._2 == (phoenixPercentage._1 * devAmount) / phoenixPercentage._2),
                (phoenixBoxOUT.propositionBytes == phoenixAddress.propBytes)
            ))
        }


        val validOutputSize: Boolean = (OUTPUTS.size == 5)

        allOf(Coll(
            validPercentages,
            validDevBoxes,
            validPhoenixBox,
            validOutputSize
        ))

    }

    sigmaProp(validFeeDistributionTx) && atLeast(1, Coll(dev1Address, dev2Address, dev3Address, phoenixAddress)) // Done so we are incentivized to not spam the miner fee.

}
