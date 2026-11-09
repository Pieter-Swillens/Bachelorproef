package io.pieterswillens.poc.guava.adapter

import com.google.common.net.InternetDomainName
import io.pieterswillens.poc.domain.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class GuavaRegistreerbaarDomeinService: RegistreerbaarDomeinService {
    override fun bepaalRegistreerbaarDomein(emailAdres: EmailAdres): BepaalRegistreerbaarDomeinResponse {
        if (!InternetDomainName.isValid(emailAdres.domeinnaam)) {
            return BepaalRegistreerbaarDomeinResponse.Ongeldig
        }

        val registererbaarDomein = InternetDomainName.from(emailAdres.domeinnaam)
        if (!registererbaarDomein.isUnderPublicSuffix) {
            return BepaalRegistreerbaarDomeinResponse.Ongeldig
        }

        return BepaalRegistreerbaarDomeinResponse.Ok(registererbaarDomein.topPrivateDomain().toString())
    }
}