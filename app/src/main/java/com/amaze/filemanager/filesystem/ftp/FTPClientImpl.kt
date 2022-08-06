/*
 * Copyright (C) 2014-2021 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.filesystem.ftp

import org.apache.commons.net.ftp.FTPClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

class FTPClientImpl(private val ftpClient: FTPClient) : NetCopyClient<FTPClient> {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(FTPClientImpl::class.java)

        @JvmStatic
        val ANONYMOUS = "anonymous"

        private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890"

        @JvmStatic
        private fun randomString(strlen: Int) = (1..strlen)
            .map { Random.nextInt(0, ALPHABET.length) }
            .map(ALPHABET::get)
            .joinToString("")

        /**
         * Generate random email address for anonymous FTP login.
         */
        @JvmStatic
        fun generateRandomEmailAddressForLogin(
            usernameLen: Int = 8,
            domainPrefixLen: Int = 5,
            domainSuffixLen: Int = 3
        ): String {
            val username = randomString(usernameLen)
            val domainPrefix = randomString(domainPrefixLen)
            val domainSuffix = randomString(domainSuffixLen)

            return "$username@$domainPrefix.$domainSuffix"
        }
    }

    override fun getClientImpl() = ftpClient

    override fun isConnectionValid(): Boolean {
        return if (ftpClient.isConnected) {
            runCatching { ftpClient.sendNoOp() }.onFailure {
                logger.warn("Failure sending NOOP to FTP server", it)
            }.getOrDefault(false)
        } else {
            false
        }
    }

    override fun isRequireThreadSafety(): Boolean = true

    override fun expire() {
        ftpClient.disconnect()
    }
}
