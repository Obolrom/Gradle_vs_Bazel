package com.romix.feature.feat229

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat229Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat229UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat229FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat229UserSummary
)

data class Feat229UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat229NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat229Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat229Config = Feat229Config()
) {

    fun loadSnapshot(userId: Long): Feat229NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat229NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat229UserSummary {
        return Feat229UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat229FeedItem> {
        val result = java.util.ArrayList<Feat229FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat229FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat229UiMapper {

    fun mapToUi(model: List<Feat229FeedItem>): Feat229UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat229UiModel(
            header = UiText("Feat229 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat229UiModel =
        Feat229UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat229UiModel =
        Feat229UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat229UiModel =
        Feat229UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat229Service(
    private val repository: Feat229Repository,
    private val uiMapper: Feat229UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat229UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat229UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat229UserItem1(val user: CoreUser, val label: String)
data class Feat229UserItem2(val user: CoreUser, val label: String)
data class Feat229UserItem3(val user: CoreUser, val label: String)
data class Feat229UserItem4(val user: CoreUser, val label: String)
data class Feat229UserItem5(val user: CoreUser, val label: String)
data class Feat229UserItem6(val user: CoreUser, val label: String)
data class Feat229UserItem7(val user: CoreUser, val label: String)
data class Feat229UserItem8(val user: CoreUser, val label: String)
data class Feat229UserItem9(val user: CoreUser, val label: String)
data class Feat229UserItem10(val user: CoreUser, val label: String)

data class Feat229StateBlock1(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock2(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock3(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock4(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock5(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock6(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock7(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock8(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock9(val state: Feat229UiModel, val checksum: Int)
data class Feat229StateBlock10(val state: Feat229UiModel, val checksum: Int)

fun buildFeat229UserItem(user: CoreUser, index: Int): Feat229UserItem1 {
    return Feat229UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat229StateBlock(model: Feat229UiModel): Feat229StateBlock1 {
    return Feat229StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat229UserSummary> {
    val list = java.util.ArrayList<Feat229UserSummary>(users.size)
    for (user in users) {
        list += Feat229UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat229UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat229UiModel {
    val summaries = (0 until count).map {
        Feat229UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat229UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat229UiModel> {
    val models = java.util.ArrayList<Feat229UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat229AnalyticsEvent1(val name: String, val value: String)
data class Feat229AnalyticsEvent2(val name: String, val value: String)
data class Feat229AnalyticsEvent3(val name: String, val value: String)
data class Feat229AnalyticsEvent4(val name: String, val value: String)
data class Feat229AnalyticsEvent5(val name: String, val value: String)
data class Feat229AnalyticsEvent6(val name: String, val value: String)
data class Feat229AnalyticsEvent7(val name: String, val value: String)
data class Feat229AnalyticsEvent8(val name: String, val value: String)
data class Feat229AnalyticsEvent9(val name: String, val value: String)
data class Feat229AnalyticsEvent10(val name: String, val value: String)

fun logFeat229Event1(event: Feat229AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat229Event2(event: Feat229AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat229Event3(event: Feat229AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat229Event4(event: Feat229AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat229Event5(event: Feat229AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat229Event6(event: Feat229AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat229Event7(event: Feat229AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat229Event8(event: Feat229AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat229Event9(event: Feat229AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat229Event10(event: Feat229AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat229Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat229Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat229(u: CoreUser): Feat229Projection1 =
    Feat229Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat229Projection1> {
    val list = java.util.ArrayList<Feat229Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat229(u)
    }
    return list
}
