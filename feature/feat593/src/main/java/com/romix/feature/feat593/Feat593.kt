package com.romix.feature.feat593

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat593Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat593UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat593FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat593UserSummary
)

data class Feat593UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat593NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat593Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat593Config = Feat593Config()
) {

    fun loadSnapshot(userId: Long): Feat593NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat593NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat593UserSummary {
        return Feat593UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat593FeedItem> {
        val result = java.util.ArrayList<Feat593FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat593FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat593UiMapper {

    fun mapToUi(model: List<Feat593FeedItem>): Feat593UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat593UiModel(
            header = UiText("Feat593 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat593UiModel =
        Feat593UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat593UiModel =
        Feat593UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat593UiModel =
        Feat593UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat593Service(
    private val repository: Feat593Repository,
    private val uiMapper: Feat593UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat593UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat593UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat593UserItem1(val user: CoreUser, val label: String)
data class Feat593UserItem2(val user: CoreUser, val label: String)
data class Feat593UserItem3(val user: CoreUser, val label: String)
data class Feat593UserItem4(val user: CoreUser, val label: String)
data class Feat593UserItem5(val user: CoreUser, val label: String)
data class Feat593UserItem6(val user: CoreUser, val label: String)
data class Feat593UserItem7(val user: CoreUser, val label: String)
data class Feat593UserItem8(val user: CoreUser, val label: String)
data class Feat593UserItem9(val user: CoreUser, val label: String)
data class Feat593UserItem10(val user: CoreUser, val label: String)

data class Feat593StateBlock1(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock2(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock3(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock4(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock5(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock6(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock7(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock8(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock9(val state: Feat593UiModel, val checksum: Int)
data class Feat593StateBlock10(val state: Feat593UiModel, val checksum: Int)

fun buildFeat593UserItem(user: CoreUser, index: Int): Feat593UserItem1 {
    return Feat593UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat593StateBlock(model: Feat593UiModel): Feat593StateBlock1 {
    return Feat593StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat593UserSummary> {
    val list = java.util.ArrayList<Feat593UserSummary>(users.size)
    for (user in users) {
        list += Feat593UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat593UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat593UiModel {
    val summaries = (0 until count).map {
        Feat593UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat593UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat593UiModel> {
    val models = java.util.ArrayList<Feat593UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat593AnalyticsEvent1(val name: String, val value: String)
data class Feat593AnalyticsEvent2(val name: String, val value: String)
data class Feat593AnalyticsEvent3(val name: String, val value: String)
data class Feat593AnalyticsEvent4(val name: String, val value: String)
data class Feat593AnalyticsEvent5(val name: String, val value: String)
data class Feat593AnalyticsEvent6(val name: String, val value: String)
data class Feat593AnalyticsEvent7(val name: String, val value: String)
data class Feat593AnalyticsEvent8(val name: String, val value: String)
data class Feat593AnalyticsEvent9(val name: String, val value: String)
data class Feat593AnalyticsEvent10(val name: String, val value: String)

fun logFeat593Event1(event: Feat593AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat593Event2(event: Feat593AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat593Event3(event: Feat593AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat593Event4(event: Feat593AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat593Event5(event: Feat593AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat593Event6(event: Feat593AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat593Event7(event: Feat593AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat593Event8(event: Feat593AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat593Event9(event: Feat593AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat593Event10(event: Feat593AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat593Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat593Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat593(u: CoreUser): Feat593Projection1 =
    Feat593Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat593Projection1> {
    val list = java.util.ArrayList<Feat593Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat593(u)
    }
    return list
}
