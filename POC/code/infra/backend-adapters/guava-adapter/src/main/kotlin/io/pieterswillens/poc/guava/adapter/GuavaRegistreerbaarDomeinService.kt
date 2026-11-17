package io.pieterswillens.poc.guava.adapter

import com.google.common.net.InternetDomainName
import io.pieterswillens.poc.domain.email.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.email.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres
import org.slf4j.LoggerFactory

class GuavaRegistreerbaarDomeinService: RegistreerbaarDomeinService {
    override fun bepaalRegistreerbaarDomein(domeinnaam: EmailAdres.Domeinnaam): BepaalRegistreerbaarDomeinResponse {
        if (!InternetDomainName.isValid(domeinnaam.value)) {
            LOGGER.warn("${domeinnaam.value} is not a valid Domeinnaam")
            return BepaalRegistreerbaarDomeinResponse.Ongeldig
        }

        val registererbaarDomein = InternetDomainName.from(domeinnaam.value)
        if (!registererbaarDomein.isUnderPublicSuffix) {
            LOGGER.warn("$registererbaarDomein does not end in a public suffix")
            return BepaalRegistreerbaarDomeinResponse.Ongeldig
        }

        return BepaalRegistreerbaarDomeinResponse.Ok(registererbaarDomein.topPrivateDomain().toString())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(GuavaRegistreerbaarDomeinService::class.java)
    }
}