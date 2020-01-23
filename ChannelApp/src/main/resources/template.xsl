<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.1" exclude-result-prefixes="fo">
    <xsl:template match="channelOrderReceiptData">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm"
                                       margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4">
                <fo:flow flow-name="xsl-region-body">

                    <fo:block font-size="8pt" font-weight="bold" space-after="0mm" border-width="1mm">
                        Channel Generated Invoice
                    </fo:block>
                    <fo:block font-size="16pt" font-weight="bold" space-after="0mm" border-width="1mm">Order ID:
                        <xsl:value-of select="orderId"/>
                    </fo:block>
                    <fo:block font-size="12pt" font-weight="bold" space-after="0mm">Channel Name:
                        <xsl:value-of select="channelName"/>
                    </fo:block>
                    <fo:block font-size="12pt" space-after="3mm">Channel Order ID:
                        <xsl:value-of select="channelOrderId"/>
                    </fo:block>

                    <fo:block font-size="10pt" font-weight="bold" space-after="5mm">Order Creation Time:
                        <xsl:value-of select="orderCreationTime"/>
                    </fo:block>

                    <fo:block font-size="10pt" font-weight="bold" space-after="8mm">
                        <fo:block font-size="10pt">
                            <fo:table table-layout="fixed" width="100%" border-collapse="separate">
                                <fo:table-column column-width="9cm"/>
                                <fo:table-column column-width="9cm"/>
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block>
												<fo:block space-after="0mm">Client Details:</fo:block>
                                                <xsl:value-of select="clientDetails"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block>
												<fo:block space-after="0mm">Customer Details:</fo:block>
                                                <xsl:value-of select="customerDetails"/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>
                    </fo:block>

                    <fo:block font-size="10pt">
                        <fo:table table-layout="fixed" width="100%" border-collapse="separate">
                            <fo:table-column column-width="4cm"/>
                            <fo:table-column column-width="5cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="4cm"/>
                            <fo:table-column column-width="5cm"/>
                            <fo:table-body>
                                <fo:table-row>

                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            OrderItem ID
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell>
                                        <fo:block>
                                            <fo:block font-weight="bold">
                                                ClientSKU ID
                                            </fo:block>
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            Quantity
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell>
                                        <fo:block>
                                            <fo:block font-weight="bold">
                                                MRP
                                            </fo:block>
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            Total
                                        </fo:block>
                                    </fo:table-cell>

                                </fo:table-row>

                                <xsl:apply-templates select="orderItems"/>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>


                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="orderItems">
        <fo:table-row>

            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="orderItemId"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="channelSkuId"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="quantity"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="mrp"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell>
                <fo:block>
                    <xsl:value-of select="total"/>
                </fo:block>
            </fo:table-cell>

        </fo:table-row>
    </xsl:template>

</xsl:stylesheet>