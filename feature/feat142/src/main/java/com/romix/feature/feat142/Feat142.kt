package com.romix.feature.feat142

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat142Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat142UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat142FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat142UserSummary
)

data class Feat142UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat142NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat142Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat142Config = Feat142Config()
) {

    fun loadSnapshot(userId: Long): Feat142NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat142NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat142UserSummary {
        return Feat142UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat142FeedItem> {
        val result = java.util.ArrayList<Feat142FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat142FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat142UiMapper {

    fun mapToUi(model: List<Feat142FeedItem>): Feat142UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat142UiModel(
            header = UiText("Feat142 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat142UiModel =
        Feat142UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat142UiModel =
        Feat142UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat142UiModel =
        Feat142UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat142Service(
    private val repository: Feat142Repository,
    private val uiMapper: Feat142UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat142UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat142UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat142UserItem1(val user: CoreUser, val label: String)
data class Feat142UserItem2(val user: CoreUser, val label: String)
data class Feat142UserItem3(val user: CoreUser, val label: String)
data class Feat142UserItem4(val user: CoreUser, val label: String)
data class Feat142UserItem5(val user: CoreUser, val label: String)
data class Feat142UserItem6(val user: CoreUser, val label: String)
data class Feat142UserItem7(val user: CoreUser, val label: String)
data class Feat142UserItem8(val user: CoreUser, val label: String)
data class Feat142UserItem9(val user: CoreUser, val label: String)
data class Feat142UserItem10(val user: CoreUser, val label: String)

data class Feat142StateBlock1(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock2(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock3(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock4(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock5(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock6(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock7(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock8(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock9(val state: Feat142UiModel, val checksum: Int)
data class Feat142StateBlock10(val state: Feat142UiModel, val checksum: Int)

fun buildFeat142UserItem(user: CoreUser, index: Int): Feat142UserItem1 {
    return Feat142UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat142StateBlock(model: Feat142UiModel): Feat142StateBlock1 {
    return Feat142StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat142UserSummary> {
    val list = java.util.ArrayList<Feat142UserSummary>(users.size)
    for (user in users) {
        list += Feat142UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat142UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat142UiModel {
    val summaries = (0 until count).map {
        Feat142UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat142UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat142UiModel> {
    val models = java.util.ArrayList<Feat142UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat142AnalyticsEvent1(val name: String, val value: String)
data class Feat142AnalyticsEvent2(val name: String, val value: String)
data class Feat142AnalyticsEvent3(val name: String, val value: String)
data class Feat142AnalyticsEvent4(val name: String, val value: String)
data class Feat142AnalyticsEvent5(val name: String, val value: String)
data class Feat142AnalyticsEvent6(val name: String, val value: String)
data class Feat142AnalyticsEvent7(val name: String, val value: String)
data class Feat142AnalyticsEvent8(val name: String, val value: String)
data class Feat142AnalyticsEvent9(val name: String, val value: String)
data class Feat142AnalyticsEvent10(val name: String, val value: String)

fun logFeat142Event1(event: Feat142AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat142Event2(event: Feat142AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat142Event3(event: Feat142AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat142Event4(event: Feat142AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat142Event5(event: Feat142AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat142Event6(event: Feat142AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat142Event7(event: Feat142AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat142Event8(event: Feat142AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat142Event9(event: Feat142AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat142Event10(event: Feat142AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat142Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat142Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat142(u: CoreUser): Feat142Projection1 =
    Feat142Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat142Projection1> {
    val list = java.util.ArrayList<Feat142Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat142(u)
    }
    return list
}
