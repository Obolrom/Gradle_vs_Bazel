package com.romix.feature.feat346

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat346Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat346UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat346FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat346UserSummary
)

data class Feat346UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat346NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat346Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat346Config = Feat346Config()
) {

    fun loadSnapshot(userId: Long): Feat346NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat346NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat346UserSummary {
        return Feat346UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat346FeedItem> {
        val result = java.util.ArrayList<Feat346FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat346FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat346UiMapper {

    fun mapToUi(model: List<Feat346FeedItem>): Feat346UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat346UiModel(
            header = UiText("Feat346 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat346UiModel =
        Feat346UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat346UiModel =
        Feat346UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat346UiModel =
        Feat346UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat346Service(
    private val repository: Feat346Repository,
    private val uiMapper: Feat346UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat346UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat346UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat346UserItem1(val user: CoreUser, val label: String)
data class Feat346UserItem2(val user: CoreUser, val label: String)
data class Feat346UserItem3(val user: CoreUser, val label: String)
data class Feat346UserItem4(val user: CoreUser, val label: String)
data class Feat346UserItem5(val user: CoreUser, val label: String)
data class Feat346UserItem6(val user: CoreUser, val label: String)
data class Feat346UserItem7(val user: CoreUser, val label: String)
data class Feat346UserItem8(val user: CoreUser, val label: String)
data class Feat346UserItem9(val user: CoreUser, val label: String)
data class Feat346UserItem10(val user: CoreUser, val label: String)

data class Feat346StateBlock1(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock2(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock3(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock4(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock5(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock6(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock7(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock8(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock9(val state: Feat346UiModel, val checksum: Int)
data class Feat346StateBlock10(val state: Feat346UiModel, val checksum: Int)

fun buildFeat346UserItem(user: CoreUser, index: Int): Feat346UserItem1 {
    return Feat346UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat346StateBlock(model: Feat346UiModel): Feat346StateBlock1 {
    return Feat346StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat346UserSummary> {
    val list = java.util.ArrayList<Feat346UserSummary>(users.size)
    for (user in users) {
        list += Feat346UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat346UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat346UiModel {
    val summaries = (0 until count).map {
        Feat346UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat346UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat346UiModel> {
    val models = java.util.ArrayList<Feat346UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat346AnalyticsEvent1(val name: String, val value: String)
data class Feat346AnalyticsEvent2(val name: String, val value: String)
data class Feat346AnalyticsEvent3(val name: String, val value: String)
data class Feat346AnalyticsEvent4(val name: String, val value: String)
data class Feat346AnalyticsEvent5(val name: String, val value: String)
data class Feat346AnalyticsEvent6(val name: String, val value: String)
data class Feat346AnalyticsEvent7(val name: String, val value: String)
data class Feat346AnalyticsEvent8(val name: String, val value: String)
data class Feat346AnalyticsEvent9(val name: String, val value: String)
data class Feat346AnalyticsEvent10(val name: String, val value: String)

fun logFeat346Event1(event: Feat346AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat346Event2(event: Feat346AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat346Event3(event: Feat346AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat346Event4(event: Feat346AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat346Event5(event: Feat346AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat346Event6(event: Feat346AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat346Event7(event: Feat346AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat346Event8(event: Feat346AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat346Event9(event: Feat346AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat346Event10(event: Feat346AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat346Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat346Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat346(u: CoreUser): Feat346Projection1 =
    Feat346Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat346Projection1> {
    val list = java.util.ArrayList<Feat346Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat346(u)
    }
    return list
}
