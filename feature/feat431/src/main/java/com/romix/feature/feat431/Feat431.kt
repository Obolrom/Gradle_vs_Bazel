package com.romix.feature.feat431

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat431Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat431UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat431FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat431UserSummary
)

data class Feat431UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat431NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat431Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat431Config = Feat431Config()
) {

    fun loadSnapshot(userId: Long): Feat431NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat431NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat431UserSummary {
        return Feat431UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat431FeedItem> {
        val result = java.util.ArrayList<Feat431FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat431FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat431UiMapper {

    fun mapToUi(model: List<Feat431FeedItem>): Feat431UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat431UiModel(
            header = UiText("Feat431 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat431UiModel =
        Feat431UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat431UiModel =
        Feat431UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat431UiModel =
        Feat431UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat431Service(
    private val repository: Feat431Repository,
    private val uiMapper: Feat431UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat431UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat431UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat431UserItem1(val user: CoreUser, val label: String)
data class Feat431UserItem2(val user: CoreUser, val label: String)
data class Feat431UserItem3(val user: CoreUser, val label: String)
data class Feat431UserItem4(val user: CoreUser, val label: String)
data class Feat431UserItem5(val user: CoreUser, val label: String)
data class Feat431UserItem6(val user: CoreUser, val label: String)
data class Feat431UserItem7(val user: CoreUser, val label: String)
data class Feat431UserItem8(val user: CoreUser, val label: String)
data class Feat431UserItem9(val user: CoreUser, val label: String)
data class Feat431UserItem10(val user: CoreUser, val label: String)

data class Feat431StateBlock1(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock2(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock3(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock4(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock5(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock6(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock7(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock8(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock9(val state: Feat431UiModel, val checksum: Int)
data class Feat431StateBlock10(val state: Feat431UiModel, val checksum: Int)

fun buildFeat431UserItem(user: CoreUser, index: Int): Feat431UserItem1 {
    return Feat431UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat431StateBlock(model: Feat431UiModel): Feat431StateBlock1 {
    return Feat431StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat431UserSummary> {
    val list = java.util.ArrayList<Feat431UserSummary>(users.size)
    for (user in users) {
        list += Feat431UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat431UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat431UiModel {
    val summaries = (0 until count).map {
        Feat431UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat431UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat431UiModel> {
    val models = java.util.ArrayList<Feat431UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat431AnalyticsEvent1(val name: String, val value: String)
data class Feat431AnalyticsEvent2(val name: String, val value: String)
data class Feat431AnalyticsEvent3(val name: String, val value: String)
data class Feat431AnalyticsEvent4(val name: String, val value: String)
data class Feat431AnalyticsEvent5(val name: String, val value: String)
data class Feat431AnalyticsEvent6(val name: String, val value: String)
data class Feat431AnalyticsEvent7(val name: String, val value: String)
data class Feat431AnalyticsEvent8(val name: String, val value: String)
data class Feat431AnalyticsEvent9(val name: String, val value: String)
data class Feat431AnalyticsEvent10(val name: String, val value: String)

fun logFeat431Event1(event: Feat431AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat431Event2(event: Feat431AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat431Event3(event: Feat431AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat431Event4(event: Feat431AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat431Event5(event: Feat431AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat431Event6(event: Feat431AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat431Event7(event: Feat431AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat431Event8(event: Feat431AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat431Event9(event: Feat431AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat431Event10(event: Feat431AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat431Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat431Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat431(u: CoreUser): Feat431Projection1 =
    Feat431Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat431Projection1> {
    val list = java.util.ArrayList<Feat431Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat431(u)
    }
    return list
}
