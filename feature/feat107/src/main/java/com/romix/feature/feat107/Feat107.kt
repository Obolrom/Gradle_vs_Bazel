package com.romix.feature.feat107

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat107Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat107UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat107FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat107UserSummary
)

data class Feat107UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat107NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat107Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat107Config = Feat107Config()
) {

    fun loadSnapshot(userId: Long): Feat107NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat107NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat107UserSummary {
        return Feat107UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat107FeedItem> {
        val result = java.util.ArrayList<Feat107FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat107FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat107UiMapper {

    fun mapToUi(model: List<Feat107FeedItem>): Feat107UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat107UiModel(
            header = UiText("Feat107 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat107UiModel =
        Feat107UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat107UiModel =
        Feat107UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat107UiModel =
        Feat107UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat107Service(
    private val repository: Feat107Repository,
    private val uiMapper: Feat107UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat107UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat107UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat107UserItem1(val user: CoreUser, val label: String)
data class Feat107UserItem2(val user: CoreUser, val label: String)
data class Feat107UserItem3(val user: CoreUser, val label: String)
data class Feat107UserItem4(val user: CoreUser, val label: String)
data class Feat107UserItem5(val user: CoreUser, val label: String)
data class Feat107UserItem6(val user: CoreUser, val label: String)
data class Feat107UserItem7(val user: CoreUser, val label: String)
data class Feat107UserItem8(val user: CoreUser, val label: String)
data class Feat107UserItem9(val user: CoreUser, val label: String)
data class Feat107UserItem10(val user: CoreUser, val label: String)

data class Feat107StateBlock1(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock2(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock3(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock4(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock5(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock6(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock7(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock8(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock9(val state: Feat107UiModel, val checksum: Int)
data class Feat107StateBlock10(val state: Feat107UiModel, val checksum: Int)

fun buildFeat107UserItem(user: CoreUser, index: Int): Feat107UserItem1 {
    return Feat107UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat107StateBlock(model: Feat107UiModel): Feat107StateBlock1 {
    return Feat107StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat107UserSummary> {
    val list = java.util.ArrayList<Feat107UserSummary>(users.size)
    for (user in users) {
        list += Feat107UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat107UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat107UiModel {
    val summaries = (0 until count).map {
        Feat107UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat107UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat107UiModel> {
    val models = java.util.ArrayList<Feat107UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat107AnalyticsEvent1(val name: String, val value: String)
data class Feat107AnalyticsEvent2(val name: String, val value: String)
data class Feat107AnalyticsEvent3(val name: String, val value: String)
data class Feat107AnalyticsEvent4(val name: String, val value: String)
data class Feat107AnalyticsEvent5(val name: String, val value: String)
data class Feat107AnalyticsEvent6(val name: String, val value: String)
data class Feat107AnalyticsEvent7(val name: String, val value: String)
data class Feat107AnalyticsEvent8(val name: String, val value: String)
data class Feat107AnalyticsEvent9(val name: String, val value: String)
data class Feat107AnalyticsEvent10(val name: String, val value: String)

fun logFeat107Event1(event: Feat107AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat107Event2(event: Feat107AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat107Event3(event: Feat107AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat107Event4(event: Feat107AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat107Event5(event: Feat107AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat107Event6(event: Feat107AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat107Event7(event: Feat107AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat107Event8(event: Feat107AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat107Event9(event: Feat107AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat107Event10(event: Feat107AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat107Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat107Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat107(u: CoreUser): Feat107Projection1 =
    Feat107Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat107Projection1> {
    val list = java.util.ArrayList<Feat107Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat107(u)
    }
    return list
}
