package mockchain

import mockClient.Common
import org.ergoplatform.appkit.InputBox
import org.ergoplatform.sdk.ErgoToken
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PhoenixTokenSpec extends AnyFlatSpec
  with Matchers
  with Common
  with PhoenixCommon {


  def hodlTokenPrice(hodlBoxIn: InputBox): Long = {
    // preserving terminology from the contract
    val reserveIn = hodlBoxIn.getTokens.get(2).getValue
    val totalTokenSupply =
      hodlBoxIn.getRegisters.get(0).getValue.asInstanceOf[Long] // R4
    val hodlCoinsIn: Long = hodlBoxIn.getTokens.get(1).getValue
    val hodlCoinsCircIn: Long = totalTokenSupply - hodlCoinsIn
    val precisionFactor = extractPrecisionFactor(hodlBoxIn)
    ((BigInt(reserveIn) * BigInt(precisionFactor)) / BigInt(
      hodlCoinsCircIn
    )).toLong
  }

  // amount of (nano) ERGs needed to mint given amount of hodlcoins against given hodl bank
  def mintAmountToken(hodlBoxIn: InputBox, hodlMintAmt: Long): Long = {
    val price = hodlTokenPrice(hodlBoxIn)
    val precisionFactor = extractPrecisionFactor(hodlBoxIn)
    hodlMintAmt * price / precisionFactor
  }


  "PhoenixTokenMintOperation" should "work correctly when all conditions are satisfied" in {

    val minAmount = 1000000L

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount)

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount),
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    noException shouldBe thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    }
  }

  "PhoenixTokenMintOperation" should "fail when more hodl taken" in {

    val minAmount = 1000000L

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount)

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount - 1), // <<-- this line has changed
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    the[Exception] thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    } should have message "Script reduced to false"
  }

  "PhoenixTokenMintOperation" should "fail when less tokens paid" in {

    val minAmount = 1000000L

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount - 1) // <<-- this line has changed

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount),
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    the[Exception] thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    } should have message "Script reduced to false"
  }

}
