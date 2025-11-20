package com.romix.feature.feat128

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat128Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat128UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat128FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat128UserSummary
)

data class Feat128UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat128NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat128Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat128Config = Feat128Config()
) {

    fun loadSnapshot(userId: Long): Feat128NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat128NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat128UserSummary {
        return Feat128UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat128FeedItem> {
        val result = java.util.ArrayList<Feat128FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat128FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat128UiMapper {

    fun mapToUi(model: List<Feat128FeedItem>): Feat128UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat128UiModel(
            header = UiText("Feat128 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat128UiModel =
        Feat128UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat128UiModel =
        Feat128UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat128UiModel =
        Feat128UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat128Service(
    private val repository: Feat128Repository,
    private val uiMapper: Feat128UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat128UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat128UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat128UserItem1(val user: CoreUser, val label: String)
data class Feat128UserItem2(val user: CoreUser, val label: String)
data class Feat128UserItem3(val user: CoreUser, val label: String)
data class Feat128UserItem4(val user: CoreUser, val label: String)
data class Feat128UserItem5(val user: CoreUser, val label: String)
data class Feat128UserItem6(val user: CoreUser, val label: String)
data class Feat128UserItem7(val user: CoreUser, val label: String)
data class Feat128UserItem8(val user: CoreUser, val label: String)
data class Feat128UserItem9(val user: CoreUser, val label: String)
data class Feat128UserItem10(val user: CoreUser, val label: String)

data class Feat128StateBlock1(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock2(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock3(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock4(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock5(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock6(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock7(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock8(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock9(val state: Feat128UiModel, val checksum: Int)
data class Feat128StateBlock10(val state: Feat128UiModel, val checksum: Int)

fun buildFeat128UserItem(user: CoreUser, index: Int): Feat128UserItem1 {
    return Feat128UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat128StateBlock(model: Feat128UiModel): Feat128StateBlock1 {
    return Feat128StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat128UserSummary> {
    val list = java.util.ArrayList<Feat128UserSummary>(users.size)
    for (user in users) {
        list += Feat128UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat128UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat128UiModel {
    val summaries = (0 until count).map {
        Feat128UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat128UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat128UiModel> {
    val models = java.util.ArrayList<Feat128UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat128AnalyticsEvent1(val name: String, val value: String)
data class Feat128AnalyticsEvent2(val name: String, val value: String)
data class Feat128AnalyticsEvent3(val name: String, val value: String)
data class Feat128AnalyticsEvent4(val name: String, val value: String)
data class Feat128AnalyticsEvent5(val name: String, val value: String)
data class Feat128AnalyticsEvent6(val name: String, val value: String)
data class Feat128AnalyticsEvent7(val name: String, val value: String)
data class Feat128AnalyticsEvent8(val name: String, val value: String)
data class Feat128AnalyticsEvent9(val name: String, val value: String)
data class Feat128AnalyticsEvent10(val name: String, val value: String)

fun logFeat128Event1(event: Feat128AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat128Event2(event: Feat128AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat128Event3(event: Feat128AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat128Event4(event: Feat128AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat128Event5(event: Feat128AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat128Event6(event: Feat128AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat128Event7(event: Feat128AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat128Event8(event: Feat128AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat128Event9(event: Feat128AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat128Event10(event: Feat128AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat128Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat128Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat128(u: CoreUser): Feat128Projection1 =
    Feat128Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat128Projection1> {
    val list = java.util.ArrayList<Feat128Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat128(u)
    }
    return list
}
